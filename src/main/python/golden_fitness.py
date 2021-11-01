# calcs the fitness of golden.js over all boards
# should be run from main directory of project

import subprocess
from pathlib import Path

NUM_OF_BOARDS = 100
GOLDEN_JS = "golden.js"

total = 0
for i in range(1, NUM_OF_BOARDS+1):
    p = subprocess.run(f'mvn exec:java@bpRun -Dexec.args="{GOLDEN_JS} {i}"', shell=True, capture_output=True)
    output = p.stdout.decode().strip()
    result = float(output.split('\n')[-1])
    print(f"{i}: {result}")
    total += result

avg = total / NUM_OF_BOARDS
print("=============")
print("Fitness is: " + avg)