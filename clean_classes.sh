#!/bin/bash

# clean_classes.sh
# Remove all compiled Java .class files recursively from the current directory.

set -e

echo "Cleaning all .class files from: $(pwd)"
find . -type f -name '*.class' -print -delete

echo "All .class files removed."
