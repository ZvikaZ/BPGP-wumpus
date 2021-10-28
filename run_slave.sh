#!/bin/bash
#SBATCH --job-name BpgpSlave
##SBATCH --mail-user=zvikah@post.bgu.ac.il
##SBATCH --mail-type=END,FAIL,REQUEU

###SBATCH --cpus-per-task=64 # 64 cpus per task - use for multithreading, usually with --tasks=1
#SBATCH --cpus-per-task=1 # 1 cpus per task - use for multithreading, usually with --tasks=1  #TODO can we increase?
#SBATCH --tasks=1 # 1 processes - use for multiprocessing
#SBATCH --mem=4G

time mvn exec:java@slave -Dexec.args="-file src/main/resources/slave.params -p eval.master.host=$1"