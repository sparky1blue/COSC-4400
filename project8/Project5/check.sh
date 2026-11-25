#!/bin/bash
# Usage: ./check.sh testcases/SomeTest.java
REF=~brylow/cosc4400/Projects
$REF/mjparser "$1" > /tmp/check.ast
$REF/mjchecker /tmp/check.ast > /tmp/check.typed.ast
$REF/mjtranslator /tmp/check.typed.ast > /tmp/ref.out
java Translate.Main < "$1" > /tmp/mine.out
diff /tmp/mine.out /tmp/ref.out && echo "MATCH: $1"