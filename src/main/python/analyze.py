import argparse
import os
import re
from matplotlib import pyplot as plt

STAT_FILE = 'out.stat'

def get_dirs(path):
    return [os.path.join(path, d) for d in os.listdir(path) if os.path.isdir(os.path.join(path, d))]


def get_run_result(d):
    with open(os.path.join(d, STAT_FILE)) as f:
        lines = f.readlines()
    with open(os.path.join(d, STAT_FILE)) as f:
        data = f.read()
    if "Best Individual's code" not in data:
        # not finished
        return
    fitness_lines = [l for l in lines if "Fitness" in l]
    r = re.compile(r'Fitness: Standardized=(\d+\.\d+) Adjusted=\d+\.\d+ Hits=0')
    fitnesses = [float(r.match(l).group(1)) for l in fitness_lines]
    assert len(fitnesses) == len(fitness_lines)
    return {
        'run': d,
        'fitnesses': fitnesses[:-1],
        'best': fitnesses[-1]
    }


def analyze_single_run(d):
    run_result = get_run_result(d)
    fig, ax = plt.subplots(1, 1)
    plot_single_run(ax, run_result)
    plt.show()


def plot_single_run(ax, r):
    out = ax.scatter(range(len(r['fitnesses'])), r['fitnesses'])
    ax.plot(r['fitnesses'])
    ax.set_xlabel("Generation")
    ax.set_ylabel("Best fitness")
    ax.set_title(r['run'])
    return out


def analyze(d, single_run):
    if single_run:
        analyze_single_run(d)
    else:
        analyze_regression(d)


def analyze_regression(regression_dir):
    results = []
    for d in get_dirs(regression_dir):
        results.append(get_run_result(d))
    results_to_plot = [r for r in results if r is not None]
    # TODO show more than 4?
    results_to_plot = results_to_plot[:6]

    fig, ax = plt.subplots(len(results_to_plot), 1, figsize=(10, 10))
    for (index, axes) in enumerate(ax):
        plot_single_run(axes, results_to_plot[index])
    fig.tight_layout()
    plt.show()



if __name__ == "__main__":
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter,
                                     description="Analyze regression results",)
    parser.add_argument("dir", help="directory containing regression, or run results")
    parser.add_argument("--single_run", "-s",  action='store_true', help="'dir' points to single run, instead of regression")

    args = parser.parse_args()
    analyze(args.dir, args.single_run)
