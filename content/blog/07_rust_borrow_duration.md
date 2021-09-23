---
date: 2021-20-01
title: "On the Duration of a Borrow"
draft: true
#twitter: ""
#reddit: ""
---

The following is interesting because we can see that the Rust compiler
is guessing when a variable remains borrowed based on the return type.

```Rust
fn f<'a>(v : &'a i32) -> &'a i32 {
    println!("GOT {}",*v);
    v
}

fn main() { 
    let mut x = 1234;
    let z = f(&x);
    let y = &mut x;
    *y = 1;
    println!("NOW {}",x);
    println!("AND {}",*z);
}
```

```
struct Empty<'a> {
    r : &'a i32  
}

fn f<'a>(v : &'a i32) -> Empty<'a> {
    println!("GOT {}",*v);
    Empty{r: v}
}

fn main() { 
    let mut x = 1234;
    let z = f(&x);
    let y = &mut x;
    *y = 1;
    println!("NOW {}",x);
    println!("AND {}",*(z.r));
}
```

```
use std::fmt;

#[derive(Debug)]
struct Empty<'a> { r : &'a i32 }

impl<'a> fmt::Display for Empty<'a> {
    // This trait requires `fmt` with this exact signature.
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f,"{}",self.r)
    }
}

fn f<'a>(v : &'a i32) -> Option<Empty<'a>> {
    println!("GOT {}",*v);
    None
}

fn main() { 
    let mut x = 1234;
    let z = f(&x);
    let y = &mut x;
    *y = 1;
    println!("NOW {}",x);
    println!("AND {}",z.unwrap());
}
```

The following fools the borrow checker:

```
use std::fmt;
use std::marker::PhantomData;

#[derive(Debug)]
struct Empty<'a> {
    phantom: PhantomData<&'a i32>
}

impl<'a> fmt::Display for Empty<'a> {
    // This trait requires `fmt` with this exact signature.
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f,"EMPTY")
    }
}

fn f<'a>(v : &'a i32) -> Empty<'a> {
    Empty{phantom:PhantomData}
}

fn main() { 
    let mut x = 1234;
    let z = f(&x);
    let y = &mut x;
    *y = 1;
    println!("NOW {}",x);
    println!("AND {}",z);
}
```

# Stuff

   * Taking two parameters (even if always the same)
   * Lifetime bounds
   * Mention paper
