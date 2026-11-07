#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p build/classes
find src/main/java -name '*.java' > build/sources-main.txt
javac --release 17 -d build/classes @build/sources-main.txt
java -cp build/classes com.algoscope.Main
