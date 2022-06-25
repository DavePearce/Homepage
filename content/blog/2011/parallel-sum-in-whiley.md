---
date: 2011-08-03
title: "Parallel Sum in Whiley"
draft: false
---

Recently, I've been working on a variety of sequential and concurrent micro benchmarks for testing Whiley's performance.  An interesting and relatively simple example, is the parallel sum.  The idea is to sum a large list of integers whilst performing as much work as possible in parallel.

To implement the parallel sum, I divide the list into roughly equal sized chunks and assign one process to each:

```whiley
define Sum as process {
    [int] items,
    int start,
    int end,
    int result
}

void Sum::start():
    sum = 0
    for i in start..end:
        sum = sum + items[i]
    this.result = sum

int Sum::get():
    return result

// Sum constructor
Sum ::Sum([int] is, int s, int e):
    return spawn {
        items: i,
        start: s,
        end: e,
        result: 0
    }
```

Essentially, each `Sum` process holds the original list of `items` and a range `start..end` over which to operate.  The `result` is used to store the final sum until it is requested by the outer loop.  The idea is that we first construct the processes, then start them all asynchronously and, finally, collect up the results.

The outer loop looks something like this:

```whiley
define N as 100 // block size to use

int ::parSum([int] items):
    while |items| != 1:
        // Calculate how many workers required
        nworkers = max(1,|items| / N)
        size = |items| / nworkers
        // Construct and start workers
        pos = 0
        workers = []
        for i in 0..nworkers:
            if i < (nworkers-1):
                worker = Sum(items,pos,pos+size)
            else:
                // Last worker picks up the slack
                worker = Sum(items,pos,|items|)
            // Start worker asynchronously
            worker!start()
            // Bookkeeping
            workers = workers + [worker]
            pos = pos + size
     // Collect up results
     items = []
     for i in 0 .. nworkers:
         items = items + [workers[i].get()]
 return items[0]
```

The key here is that the outer loop continues until the original list of `items` is reduced to a single result.  There maybe several iterations required, depending on the block size.  Furthermore, the block size determines how many items will be processed by each process in one go.  Smaller block sizes lead to more parallelism, but have higher overheads.  The optimal block size probably depends on the underlying architecture, and would ideally be chosen at runtime.