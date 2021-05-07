#!/bin/bash

# sends 20 sbatch runs
# should be run only from its directory!

orig_dir=$PWD
regression_dir=target/regression/`date "+%F-%T"`
rm -rf $regression_dir
mkdir -p $regression_dir
for i in {1..20}
do
    mkdir $regression_dir/$i
    cd $regression_dir/$i
    pwd
    sbatch $orig_dir/run.sh
    cd $orig_dir
done

