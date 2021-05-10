#!/bin/bash
#SBATCH --job-name BpgpEvolve
#SBATCH --mail-user=zvikah@post.bgu.ac.il
##SBATCH --mail-type=END,FAIL,REQUEU

#SBATCH --cpus-per-task=8 # 8 cpus per task - use for multithreading, usually with --tasks=1
#SBATCH --tasks=1 # 1 processes - use for multiprocessing

#TODO relative paths
time /home/zvikah/.jdks/openjdk-16.0.1/bin/java -Dfile.encoding=UTF-8 -classpath /home/zvikah/projects/bpgp/First/target/classes:/home/zvikah/.m2/repository/edu/gmu/cs/ecj/27/ecj-27.jar:/home/zvikah/.m2/repository/com/github/bthink-bgu/BPjs/0.12.0/BPjs-0.12.0.jar:/home/zvikah/.m2/repository/org/mozilla/rhino/1.7.13/rhino-1.7.13.jar:/home/zvikah/.m2/repository/commons-io/commons-io/2.5/commons-io-2.5.jar BpgpEvolve


