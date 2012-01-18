#!/bin/bash
#This script is used in conjuction with generate_results. A basic usage is as follows:
#scripts/output.sh | scripts/generate_results.py > output.html. This will output the results
#of every classifier in a table.
mvn test  | grep -E "General (precision|recall)"
