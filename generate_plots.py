import os
import csv
import matplotlib.pyplot as plt

os.makedirs('results/plots', exist_ok=True)

results = []
with open('results/results.csv', 'r', encoding='utf-8') as f:
    reader = csv.DictReader(f)
    for row in reader:
        results.append({
            'workload': row['workload'],
            'variant': row['variant'],
            'structure': row['structure'],
            'n': int(row['n']),
            'time_ms': float(row['time_ms']),
            'steps': int(row['steps']),
            'moves': int(row['moves']),
            'comparisons': int(row['comparisons'])
        })

def filter_res(workload, variant=None, structure=None):
    filtered = [r for r in results if r['workload'] == workload]
    if variant is not None:
        filtered = [r for r in filtered if r['variant'] == variant]
    if structure is not None:
        filtered = [r for r in filtered if r['structure'] == structure]
    return sorted(filtered, key=lambda x: x['n'])

plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')
plt.rcParams.update({'font.sans-serif': 'DejaVu Sans', 'font.size': 11})

w1_da = filter_res('W1', structure='DynamicArray')
w1_ll = filter_res('W1', structure='MyLinkedList')
ns = [r['n'] for r in w1_da]

plt.figure(figsize=(8, 5))
plt.plot(ns, [r['time_ms'] for r in w1_da], marker='o', linewidth=2, label='DynamicArray O(1) get')
plt.plot(ns, [r['time_ms'] for r in w1_ll], marker='s', linewidth=2, label='MyLinkedList O(n) get')
plt.xscale('log')
plt.yscale('log')
plt.xlabel('Dataset Size (n)')
plt.ylabel('Execution Time (ms, log scale)')
plt.title('W1 - Random Access: Execution Time vs n (10,000 get calls)')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/w1_time_vs_n.png', dpi=300)
plt.close()

plt.figure(figsize=(8, 5))
plt.plot(ns, [r['steps'] for r in w1_da], marker='o', linewidth=2, label='DynamicArray Steps (10,000 const)')
plt.plot(ns, [r['steps'] for r in w1_ll], marker='s', linewidth=2, label='MyLinkedList Steps (linear with n)')
plt.xscale('log')
plt.yscale('log')
plt.xlabel('Dataset Size (n)')
plt.ylabel('Physical Steps (log scale)')
plt.title('W1 - Random Access: Physical Steps vs n (10,000 get calls)')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/w1_ops_vs_n.png', dpi=300)
plt.close()

w2_da = filter_res('W2', structure='DynamicArray')
w2_ll = filter_res('W2', structure='MyLinkedList')

plt.figure(figsize=(8, 5))
plt.plot(ns, [r['time_ms'] for r in w2_da], marker='o', linewidth=2, label='DynamicArray (Cache Local)')
plt.plot(ns, [r['time_ms'] for r in w2_ll], marker='s', linewidth=2, label='MyLinkedList (Pointer Chasing)')
plt.xscale('log')
plt.yscale('log')
plt.xlabel('Dataset Size (n)')
plt.ylabel('Execution Time (ms, log scale)')
plt.title('W2 - Linear Search: Execution Time vs n (1,000 queries)')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/w2_time_vs_n.png', dpi=300)
plt.close()

plt.figure(figsize=(8, 5))
plt.plot(ns, [r['steps'] for r in w2_da], marker='o', linewidth=2, label='DynamicArray Steps')
plt.plot(ns, [r['steps'] for r in w2_ll], marker='s', linewidth=2, linestyle='--', label='MyLinkedList Steps')
plt.plot(ns, [r['comparisons'] for r in w2_da], marker='^', linewidth=2, linestyle=':', label='Comparisons (both identical)')
plt.xscale('log')
plt.yscale('log')
plt.xlabel('Dataset Size (n)')
plt.ylabel('Operation Count (log scale)')
plt.title('W2 - Search: Physical Steps & Comparisons vs n')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/w2_ops_vs_n.png', dpi=300)
plt.close()

w3h_da = filter_res('W3', variant='head', structure='DynamicArray')
w3h_ll = filter_res('W3', variant='head', structure='MyLinkedList')

plt.figure(figsize=(8, 5))
plt.plot(ns, [r['time_ms'] for r in w3h_da], marker='o', linewidth=2, label='DynamicArray Head (O(n) shift)')
plt.plot(ns, [r['time_ms'] for r in w3h_ll], marker='s', linewidth=2, label='MyLinkedList Head (O(1) pointer)')
plt.xscale('log')
plt.yscale('log')
plt.xlabel('Dataset Size (n)')
plt.ylabel('Execution Time (ms, log scale)')
plt.title('W3 (Head) - 1,000 Insert & Remove at Index 0: Time vs n')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/w3_head_time_vs_n.png', dpi=300)
plt.close()

plt.figure(figsize=(8, 5))
plt.plot(ns, [r['moves'] for r in w3h_da], marker='o', linewidth=2, label='DynamicArray Moves (element shifts)')
plt.plot(ns, [r['moves'] for r in w3h_ll], marker='s', linewidth=2, label='MyLinkedList Moves (5,000 const link updates)')
plt.xscale('log')
plt.yscale('log')
plt.xlabel('Dataset Size (n)')
plt.ylabel('Physical Moves (log scale)')
plt.title('W3 (Head) - Physical Moves vs n')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/w3_head_ops_vs_n.png', dpi=300)
plt.close()

w3m_da = filter_res('W3', variant='middle', structure='DynamicArray')
w3m_ll = filter_res('W3', variant='middle', structure='MyLinkedList')

plt.figure(figsize=(8, 5))
plt.plot(ns, [r['time_ms'] for r in w3m_da], marker='o', linewidth=2, label='DynamicArray Middle (Shift O(n))')
plt.plot(ns, [r['time_ms'] for r in w3m_ll], marker='s', linewidth=2, label='MyLinkedList Middle (Traverse O(n))')
plt.xscale('log')
plt.yscale('log')
plt.xlabel('Dataset Size (n)')
plt.ylabel('Execution Time (ms, log scale)')
plt.title('W3 (Middle) - 1,000 Insert & Remove at n/2: Time vs n')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/w3_middle_time_vs_n.png', dpi=300)
plt.close()

plt.figure(figsize=(8, 5))
plt.plot(ns, [r['steps'] for r in w3m_da], marker='o', linewidth=2, label='DynamicArray Steps (reads)')
plt.plot(ns, [r['moves'] for r in w3m_da], marker='v', linewidth=2, label='DynamicArray Moves (shifts)')
plt.plot(ns, [r['steps'] for r in w3m_ll], marker='s', linewidth=2, label='MyLinkedList Steps (traversal hops)')
plt.plot(ns, [r['moves'] for r in w3m_ll], marker='^', linewidth=2, label='MyLinkedList Moves (link updates)')
plt.xscale('log')
plt.yscale('log')
plt.xlabel('Dataset Size (n)')
plt.ylabel('Physical Operations (log scale)')
plt.title('W3 (Middle) - Physical Steps and Moves vs n')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/w3_middle_ops_vs_n.png', dpi=300)
plt.close()

w4_mh = filter_res('W4', structure='MinHeap')

fig, ax1 = plt.subplots(figsize=(8, 5))
ax2 = ax1.twinx()

p1 = ax1.plot(ns, [r['time_ms'] for r in w4_mh], color='crimson', marker='o', linewidth=2, label='Time (ms)')
p2 = ax2.plot(ns, [r['steps'] for r in w4_mh], color='royalblue', marker='s', linewidth=2, label='Steps')
p3 = ax2.plot(ns, [r['moves'] for r in w4_mh], color='seagreen', marker='^', linewidth=2, label='Moves')
p4 = ax2.plot(ns, [r['comparisons'] for r in w4_mh], color='darkorange', marker='d', linewidth=2, label='Comparisons')

ax1.set_xscale('log')
ax1.set_yscale('log')
ax2.set_yscale('log')

ax1.set_xlabel('Dataset Size (n)')
ax1.set_ylabel('Execution Time (ms, log scale)', color='crimson')
ax2.set_ylabel('Physical Operations Count (log scale)', color='royalblue')
plt.title('W4 - MinHeap Priority Processing: Time & Operations vs n')

lines = p1 + p2 + p3 + p4
labels = [l.get_label() for l in lines]
ax1.legend(lines, labels, loc='upper left')
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/w4_time_and_ops_vs_n.png', dpi=300)
plt.close()

mem_records = {'DynamicArray': [], 'MyLinkedList': [], 'MinHeap': []}
with open('results/memory_footprint.csv', 'r', encoding='utf-8') as f:
    reader = csv.DictReader(f)
    for row in reader:
        mem_records[row['structure']].append((int(row['n']), float(row['mb'])))

plt.figure(figsize=(8, 5))
for struct, vals in mem_records.items():
    vals.sort(key=lambda x: x[0])
    plt.plot([v[0] for v in vals], [v[1] for v in vals], marker='o', linewidth=2, label=struct)

plt.xscale('log')
plt.yscale('log')
plt.xlabel('Dataset Size (n)')
plt.ylabel('Memory Footprint (MB, log scale)')
plt.title('Bonus Task A: Memory Footprint vs n (JOL Analysis)')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.5)
plt.tight_layout()
plt.savefig('results/plots/bonus_memory_vs_n.png', dpi=300)
plt.close()

floyd_records = {'Floyd_O(n)': [], 'Sequential_O(n_log_n)': []}
with open('results/floyd_vs_insert.csv', 'r', encoding='utf-8') as f:
    reader = csv.DictReader(f)
    for row in reader:
        floyd_records[row['method']].append({
            'n': int(row['n']),
            'time_ms': float(row['time_ms']),
            'steps': int(row['steps']),
            'moves': int(row['moves']),
            'comparisons': int(row['comparisons'])
        })

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))

floyd_n = [r['n'] for r in floyd_records['Floyd_O(n)']]
floyd_comps = [r['comparisons'] for r in floyd_records['Floyd_O(n)']]
seq_comps = [r['comparisons'] for r in floyd_records['Sequential_O(n_log_n)']]

floyd_times = [r['time_ms'] for r in floyd_records['Floyd_O(n)']]
seq_times = [r['time_ms'] for r in floyd_records['Sequential_O(n_log_n)']]

ax1.plot(floyd_n, floyd_comps, marker='o', linewidth=2, label='Floyd buildHeap O(n)')
ax1.plot(floyd_n, seq_comps, marker='s', linewidth=2, label='Sequential Inserts O(n log n)')
ax1.set_xscale('log')
ax1.set_yscale('log')
ax1.set_xlabel('Dataset Size (n)')
ax1.set_ylabel('Comparisons Count (log scale)')
ax1.set_title('Comparisons: Floyd vs Sequential Inserts')
ax1.legend()
ax1.grid(True, which="both", ls="--", alpha=0.5)

ax2.plot(floyd_n, floyd_times, marker='o', linewidth=2, label='Floyd buildHeap O(n)')
ax2.plot(floyd_n, seq_times, marker='s', linewidth=2, label='Sequential Inserts O(n log n)')
ax2.set_xscale('log')
ax2.set_yscale('log')
ax2.set_xlabel('Dataset Size (n)')
ax2.set_ylabel('Execution Time (ms, log scale)')
ax2.set_title('Execution Time: Floyd vs Sequential Inserts')
ax2.legend()
ax2.grid(True, which="both", ls="--", alpha=0.5)

plt.tight_layout()
plt.savefig('results/plots/bonus_floyd_vs_insert.png', dpi=300)
plt.close()
