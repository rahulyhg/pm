package com.finance.pms.events.calculation.antlr;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTree;

import com.finance.pms.events.calculation.antlr.EditorOpDescr.Param;
import com.finance.pms.events.calculation.antlr.EditorOpDescr.ParamType;
import com.finance.pms.events.calculation.antlr.ParamsCountException.Qualifier;

public class EditorOpsParserDelegate extends EditorParserDelegate implements OpsParserDelegate {

	@SuppressWarnings("unused")
	private RecognizerSharedState state;

	private LinkedList<EditorOpDescr> needClosingOpsStack;

	private int currentParamPos;
	
	  public EditorOpsParserDelegate(TokenStream input, RecognizerSharedState state,  Set<EditorOpDescr> runtimeNativeOps, Set<EditorOpDescr> runtimeUserOps) {
		  super(input, runtimeNativeOps,runtimeUserOps);
	      this.state = state;
	      
	      this.allNativeOps =  aggAllOperations(runtimeNativeOps, runtimeUserOps);
	      
	      this.needClosingOpsStack =  new LinkedList<EditorOpDescr>();
	  }


	public Boolean checkParamExhaust(Token currentOpToken, List<Object> paramsTree) throws ParamsCountException, UnfinishedParameterException, InvalidOperationException {

		EditorOpDescr currentOp = grabOpForToken(currentOpToken);
		
		if (currentOp == null) {
			System.out.println("I Should not be here : checking params " + paramsTree + " but no current op !!!!!??????? ");
			throw new InvalidOperationException(input, currentOpToken, paramsTree);
		}
		
		int commas = 0;
		List<Object> params = new ArrayList<Object>();
		if (paramsTree != null && paramsTree.size()==1) {
			if (paramsTree.get(0) instanceof CommonTree && ((CommonTree)paramsTree.get(0)).isNil()) {//Root of several params
				//params.addAll(children);
				List<? extends Object> children = ((CommonTree) paramsTree.get(0)).getChildren();
				for (Object param : children) {
					if (param.toString().equals("StockOperation")) {
						params.add(((CommonTree)param).getChild(0).getChild(0));
					} else {
						params.add(param);
					}
				}
				commas = params.size() -1;
			} else {//One param
				//params.add(((CommonTree) paramsTree.get(0)));
				Object param = paramsTree.get(0);
				if (param.toString().equals("StockOperation")) {
					params.add(((CommonTree)param).getChild(0).getChild(0));
				} else {
					params.add(param);
				}
				commas = 0;
			}
		} 
		
		currentOp.setNbCommasParsed(commas);

		System.out.println("checking params " + params + " against "+currentOp.getSynoptic());
		currentParamPos = 0;
		for (; (currentParamPos < params.size()) && (currentParamPos < currentOp.getParams().size() || currentOp.undeterministicParamCount()); currentParamPos++) {

			Object token = params.get(currentParamPos);
			//currentOp.setLastParamParsed(currentParamPos, getTheSonOf(token));

			//Params Type check
			ParamType paramType = null;

			if (token instanceof CommonErrorNode) {//Parsing error

				String  paramTxt = ((CommonErrorNode) token).getText();
				System.out.println("Error node " + ((CommonErrorNode) token) + " with text "+ paramTxt);

				String offendingBit = ((CommonErrorNode) token).getText().replaceAll("\n"," ").replaceAll("\\(.*", "").replaceAll(",.*", "").trim();
				if (offendingBit != null && !offendingBit.isEmpty()) {

					try {

						//Type check on the offending bit
						paramType = ParamType.valueOfTokenName(offendingBit, allNativeOps);
						//No runtime was raise so we assume the offending bit is ok as the last param <= what if it not the last one?
						String msg = "Unfinished predicate (unfinished param) for "+ currentOp.getName() +" : Valid offending bit "+offendingBit+". Expected : "+currentOp.getShortSynoptic();
						System.out.println(msg+ " "+token);
						//This is to avoid the FailedPredicateException to be raised but without having to return true.
						//We can return true only when this param is finished.
						//And a FailedPredicateException would mean that is param is still needed as it is not.
						currentOp.setLastParamParsed(currentParamPos, offendingBit);
						throw new UnfinishedParameterException(input, currentOp, paramType, offendingBit);

					} catch (RuntimeException e) {

						String msg = "Syntax error or invalid parameter '"+offendingBit+"' for "+currentOp.getName()+". Expected : "+currentOp.getShortSynoptic();
						System.out.println(msg+ " "+token);
						currentOp.setLastParamParsed(currentParamPos, offendingBit);
						throw new ParamsCountException(input, currentOp, msg, true, Qualifier.SYNTAX, offendingBit, whereIsThatSonOf(token));

					}

				} else {

					//Nothing yet to check
					String msg = "Unfinished predicate (empty param ) for "+ currentOp.getName() +" : Empty offending bit "+offendingBit+". Expected : "+currentOp.getShortSynoptic();
					System.out.println(msg+ " "+token);
					currentOp.setLastParamParsed(currentParamPos, offendingBit);
					throw new UnfinishedParameterException(input,currentOp, paramType, offendingBit);
				}



			} else if (token instanceof CommonTree) {// No parsing error occured (we still have to check the param types)

				CommonTree cTree = ((CommonTree)token);
				String paramTxt = cTree.getText();
				paramType = ParamType.valueOfTokenName(paramTxt, allNativeOps);

			}

			Param expectedParam = (currentOp.undeterministicParamCount())?currentOp.getParams().get(0):currentOp.getParams().get(currentParamPos);
			if (!expectedParam.getParamType().equals(paramType)) {

				String msg = "Wrong parameter type '"+paramType.getTypeDescr()+"' for "+currentOp.getName()+". Expected : "+currentOp.getShortSynoptic();
				System.out.println(msg+ " "+token);
				String theSonOf = getTheSonOf(token);
				currentOp.setLastParamParsed(currentParamPos, theSonOf);
				throw new ParamsCountException(input, currentOp, msg, true, Qualifier.TYPE, theSonOf, whereIsThatSonOf(token));

			}

		} 

		//Not enough params
		if (params.size() < currentOp.getParams().size()) {
			String msg = "Unfinished predicate (missing params in "+params+") for "+currentOp.getName()+". Expected : "+currentOp.getShortSynoptic();
			System.out.println(msg+ " "+params);
			String paramsStr = "";
			int [] position = null;
			if (params.size() > 0) {
				paramsStr = getTheSonOf(params.toArray(new Object[] {}));
				position = whereIsThatSonOf(params.get(0));
			}
			currentOp.setLastParamParsed(currentParamPos, paramsStr);
			throw new ParamsCountException(input, currentOp, msg, true, Qualifier.NOTENOUGHARGS, paramsStr, position);
		} 
		//Too many params
		else if (params.size() > currentOp.getParams().size() && !currentOp.undeterministicParamCount() ) {
			String paramsStr = getTheSonOf(params.toArray(new Object[]{}));
			String msg = "Too many arguments '"+paramsStr+"' for "+currentOp.getName()+". Expected : "+currentOp.getShortSynoptic();
			System.out.println(msg+ " "+params);
			currentOp.setLastParamParsed(currentOp.getParams().size()-1, paramsStr);
			throw new ParamsCountException(input, currentOp, msg, true, Qualifier.TOOMANYARGS, paramsStr, whereIsThatSonOf(params.get(0)));
		}	
		else if (params.size() == currentOp.getParams().size() || (params.size() >= 1 && currentOp.undeterministicParamCount())) {
			try {
				needClosingOpsStack.offerLast((EditorOpDescr) currentOp.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return true;
		}

		System.out.println("I Should not be here : checking params count unatched !!!????" + params + ". Expected "+currentOp.getSynoptic());
		return false;

	}

	private String getTheSonOf(Object... params) {

		String sonsStr = "";
		String sep = "";
		for(Object son : params) {

			String paramTxt = "";
			if (son instanceof CommonErrorNode) {
				paramTxt = ((CommonErrorNode) son).getText();
			} else if (son instanceof CommonTree) {
				CommonTree cTree = ((CommonTree)son);
				paramTxt = ((CommonTree) son).getText();
				if (paramTxt.equals("Double") || paramTxt.equals("Stock")  || paramTxt.equals("MAType")) {
					paramTxt = cTree.getChild(0).getText();
				} else {
					paramTxt = cTree.getText();
				}
			}

			sonsStr = sonsStr + sep + paramTxt;
			sep = ",";
		}
		return sonsStr;
	}

	private int[] whereIsThatSonOf(Object son) {

		int[] location = new int[2];
		if (son instanceof CommonToken) {
			location[0] = ((CommonToken) son).getLine();
			location[1] = ((CommonToken) son).getCharPositionInLine();
		} else  if (son instanceof CommonErrorNode) {
			location[0] = ((CommonErrorNode) son).start.getLine();
			location[1] = ((CommonErrorNode) son).start.getCharPositionInLine();
		} else if (son instanceof CommonTree) {
			CommonTree cTree = ((CommonTree)son);
			if (cTree.getText().equals("Double") || cTree.getText().equals("Stock") ) {
				location[0] = cTree.getChild(0).getLine();
				location[1] = cTree.getChild(0).getCharPositionInLine();
			} else {
				location[0] = cTree.getLine();
				location[1] = cTree.getCharPositionInLine();
			}
		}

		return location;
	}

	public Set<EditorOpDescr> getAllNativeOps() {
		return allNativeOps;
	}

	public int getCurrentParamPos() {
		return currentParamPos;
	}
	
	public EditorOpDescr doesNeedClosing() {
		if (needClosingOpsStack.size() > 0) {
			EditorOpDescr ret = needClosingOpsStack.pollLast();
			System.out.println("peek last op for closing :"+ret.getName());
			return ret;
		}
		return null;
	}
}
