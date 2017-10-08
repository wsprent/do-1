#!/usr/bin/env bash

instance=$1
repeat=$2
if [ ! -f ./branch.csv ]; then
    echo 'instance,path,nodes,time(ms)' > ./branch.csv
fi

if [ ! -f ./cplex.csv ]; then
    echo 'instance,path,time(ms)' > ./cplex.csv
fi

echo "Running tests marked as instance $1, $2 times"
for ((i=1; i <= $2; i++)); do
    (make run 2>/dev/null | tail -n +2 | grep -o -P '([0-9]+(\.[0-9]+)?)|BRANCH|CPLEX'; echo $1) | xargs | awk '{OFS=","}{print $8, $2, $3, $4 >> "branch.csv"}{print $8, $6, $7 >> "cplex.csv"}'
    echo -ne "$i/$2\r"
done;
