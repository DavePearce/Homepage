---
date: 2021-20-01
title: "Trying out the Rust Model Checker (RMC)"
draft: true
#twitter: ""
#reddit: ""
---

Intro RMC.  Intro method for verification.

```Rust
fn indexof(items: &[u32], item: u32) -> usize {
    for i in 0..items.len() {
	if items[i] == item {
            return i;
	}
    }
    //
    return usize::MAX;
}
```

## First GO

Do something simple.

## Getting more sophisticated

Generalising

## Good Stuff

Final solution.