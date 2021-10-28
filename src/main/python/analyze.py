#!/home/zvikah/.conda/envs/bpgp/bin/python

import argparse
import os
import re
from matplotlib import pyplot as plt


STAT_FILE = 'bpgp.stat'

def get_dirs(path):
    return [os.path.join(path, d) for d in os.listdir(path) if os.path.isdir(os.path.join(path, d))]


def get_run_result(d):
    with open(os.path.join(d, STAT_FILE)) as f:
        lines = f.readlines()

    # Generation 0, best: 47.000000, mean: 423.200000, median: 74.000000, num_of_inds: 10, unique_values: 3, unique_ratio: 0.300000
    r = re.compile(r'Generation (\d+), best: (\d+\.\d+), mean: (\d+\.\d+|Infinity), median: (\d+\.\d+), .*, unique_ratio: (\d+\.\d+)')
    generations = [int(r.match(l).group(1)) for l in lines]
    bests = [float(r.match(l).group(2)) for l in lines]
    means = [float(r.match(l).group(3)) for l in lines]
    medians = [float(r.match(l).group(4)) for l in lines]
    unique_ratios = [float(r.match(l).group(5)) for l in lines]
    assert len(lines) == len(generations) == len(bests) == len(means) == len(medians) == len(unique_ratios)
    if float('inf') in means:
        print("Warning: there is an inifinity in means: ", means)
    return {
        'run': d,
        'bests': bests,
        'means': means,
        'medians': medians,
        'unique_ratios': unique_ratios,
    }


def analyze_single_run(d):
    run_result = get_run_result(d)
    fig, ax = plt.subplots(1, 1)
    plot_single_run(ax, run_result)
    ax.legend()
    plt.show()


def plot_single_run(ax, r):
    out = ax.plot(r['bests'], 'o-', label='best')
    ax.plot(r['means'], 'o-', label='mean')
    ax.plot(r['medians'], 'o-', alpha=0.6, label='median')
    # plot nothing, just add to our legend
    ax.plot([], [], '.-', label = 'unique_ratios', color='tab:pink')
    ax.set_ylim(bottom=0)
    ax.set_xlabel("Generation")
    ax.set_ylabel("Fitness")
    ax.set_title(r['run'])

    ax2 = ax.twinx()
    ax2.set_ylabel("Unique fitness val ratio")
    ax2.set_ylim([0,1])
    ax2.plot(r['unique_ratios'], '.-', label='unique_ratios', color='tab:pink')
    # create a separate legend
    # ax2.legend(loc=2)

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
    axes.legend(loc='lower left')
    fig.tight_layout()
    plt.show()



if __name__ == "__main__":
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter,
                                     description="Analyze regression results",)
    parser.add_argument("dir", help="directory containing regression, or run results")
    parser.add_argument("--single_run", "-s",  action='store_true', help="'dir' points to single run, instead of regression")

    args = parser.parse_args()
    analyze(args.dir, args.single_run)
