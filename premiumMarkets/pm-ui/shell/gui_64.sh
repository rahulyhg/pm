#!/bin/bash

export SWT_GTK3=0
export LIBOVERLAY_SCROLLBAR=0

BN=`basename "$0"`
INSTALLDIR=`expr "$0" : '\(.*\)'"$BN"`..

echo "install dir "$INSTALLDIR

INSTALLDIRFIRST=`echo "$INSTALLDIR" | cut -c1-1`
echo "start  : "$INSTALLDIRFIRST
if [ "$INSTALLDIRFIRST" != "/" ];
	then
		INSTALLDIR="$PWD"/"$INSTALLDIR";
fi

if [ -n "$1" ];
	then
		INSTALLDIR="$1";
fi

echo "install dir :"$INSTALLDIR

echo $PATH
java -version

cd "$INSTALLDIR"
	
nohup java -splash:"$INSTALLDIR"/icons/squeakyPig.png -Xmx4G -Xss228k -Dsite.url=premiummarkets.uk -Djava.library.path="$INSTALLDIR"/lib -Dinstalldir="$INSTALLDIR" -jar "$INSTALLDIR"/pm.jar "$INSTALLDIR"/db.properties > ~/tmp/pm.out &


sleep 15



