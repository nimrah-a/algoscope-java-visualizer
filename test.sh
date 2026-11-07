#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p build/classes
find src/main/java src/test/java -name '*.java' > build/sources-test.txt
javac --release 17 -d build/classes @build/sources-test.txt
java -ea -cp build/classes com.algoscope.EngineTest
