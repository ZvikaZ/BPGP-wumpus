#!/bin/bash
#SBATCH --job-name BpgpMaster
#SBATCH --mail-user=zvikah@post.bgu.ac.il
#SBATCH --mail-type=END,FAIL,REQUEU

#SBATCH --cpus-per-task=64 # 64 cpus per task - use for multithreading, usually with --tasks=1
#SBATCH --cpus-per-task=1 # 1 cpus per task - use for multithreading, usually with --tasks=1

./slaves_farm.sh
time mvn exec:java@master
