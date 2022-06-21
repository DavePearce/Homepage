---
date: 2012-02-29
title: "A Problem of Decoupling?"
draft: false
---

Recently, I've been working on improving the core framework that underpins the Whiley compiler.  This provides a platform for reading/writing files of specified content in a structured fashion.  Like Java, Whiley provides a hierarchical namespace in which names live and can be imported by others.  Let's consider a simple example:

```whiley
package zlib.core

import Console from whiley.lang.System
import zlib.util.BitBuffer
```

Here, we have two modules that must exist in the global namespace: `whiley.lang.System` and `zlib.util.BitBuffer`.  As with Java, they co-exist in the same namespace, but do not necessarily originate from the same physical location (i.e. `whiley.lang.System` is located in the `wyrt.jar`, whilst `zlib.util.BitBuffer` is in a file system directory somewhere).

The Whiley compiler takes care of this through the `Path.ID` and `Path.Root` abstractions.  A `Path.ID` represents a hierarchical name in the global namespace (e.g. `whiley.lang.System`); a `Path.Root` represents a physical location which forms the root of a name hierarchy (e.g. a `jar` file or a directory).  Thus, the global namespace is made up from multiple roots and, to find a given item, we traverse them looking for it (we'll ignore the possibility of collisions for simplicity).  To illustrate, here's the (slightly simplified) `Path.ID` interface:

```java
public interface ID {

  /**
   * Get number of components in this ID.
   * ...
   */
  public int size();

  /**
   * Return the component at a given index.
   * ...
   */
  public String get(int index);

  /**
   * Get last component of this path ID.
   * ...
   */
  public String last();

  /**
   * Get parent of this path ID.
   * ...
   */
  public ID parent();

  /**
   * Append component onto end of this id.
   * ...
   */
  public ID append(String component);
}
```


This all seems simple enough, right?  *Well, yeah it is!**So, what's up? * Well, the thing is, the Whiley compiler is not the first system to address this problem!  Eclipse, for example, adopts a similar approach through the `IPath` interface.  A cut-down version of this interface is:

```java
public interface IPath {
  /**
   * Returns the specified segment of this path, ...
   * ...
   */
  public String segment(int index);

  /**
   * Returns the last segment of this path, ...
   * ...
   */
  public String lastSegment();

  /**
   * Returns the number of segments in this path.
   * ...
   */
  public int segmentCount();

  /**
   * Returns whether this path is a prefix of the given path.
   * ...
   */
  public int isPrefixOf(IPath path);

  /**
   * Return absolute path with segments and device id.
   * ...
   */
  public IPath makeAbsolute(IPath path);

  ...
}
```

Hopefully, you'll notice both similarity and difference between the `Path.ID` and `IPath` interfaces.  In fact, `IPath` has quite a few more methods which are not present in `Path.ID`.  However, it should be clear that the functionality described by `Path.ID` is a subset of that described by `IPath` (albeit with slightly different names).

Obviously, I want to integrate the Whiley compiler with Eclipse (i.e. make an Eclipse plugin for Whiley).  At the same time, I want to reuse as much of Eclipse's functionality as possible within my plugin --- otherwise, I'm just adding more bloat to an already bloated system, and potentially compromising the effectiveness of my plugin.  I'm prepared to go to some lengths to enable this, even to the point of changing my `Path.ID` interface to bring it more inline with `IPath`; however, *I'm not prepared to make the Whiley Compiler depend upon Eclipse* --- that is, no `import org.eclipse..*` statements in the Whiley compiler.

*How can I do this?* Well, the obvious solution is to provide an [Adaptor](http://en.wikipedia.org/wiki/Adapter_pattern) in the plugin which implements `Path.ID` and wraps an `IPath`.  This probably requires adding a [Factory](http://en.wikipedia.org/wiki/Abstract_factory_pattern) interface (e.g. `Path.Factory`) for creating `Path.ID` instances, since the Whiley compiler needs the ability to construct `Path.ID` instances and test for their validity.

*The adaptor works, right?* Yeah, it does.  But, it amounts to layering one abstraction on top of another *when they're really the same thing*.  It would be nice if there was a  mechanism for binding abstractions together.  For example, in the Eclipse Plugin, it would let me say: *let an `IPath` be a valid `Path.ID` with the following binding between names*.  But, perhaps that's too much wishful thinking...
