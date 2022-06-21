---
date: 2012-02-18
title: "Writing a PNG Decoder in Whiley!"
draft: false
---

Over the last few days, I have been writing GIF and PNG decoders in Whiley.  These form part of an image manipulation benchmark which I'm planning to use for experimenting with the compiler.  The PNG decoder, in particular, was rather interesting.  My implementation is based directly off the [PNG Specification, ](http://www.w3.org/TR/2003/REC-PNG-20031110/) [ISO/IEC 15948:2003 (E)](http://www.w3.org/TR/2003/REC-PNG-20031110/). The PNG format uses the [DEFLATE algorithm](http://en.wikipedia.org/wiki/DEFLATE) for decompression (as used by e.g. GZip).  This was convenient since I'd already implemented DEFLATE in Whiley!

You can find the complete code for my PNG decoder [here](https://github.com/DavePearce/wybench/tree/master/sequential/convert/imagelib/png).  To compile it, you'll probably need to checkout the latest [development snapshot of the Whiley compiler](https://github.com/DavePearce/Whiley/tree/devel).  At the moment, it doesn't run too fast either ... but that's mostly because arithmetic in Whiley is unbounded and, hence, relatively slow. Eventually, I will optimise most of this overhead away.

## Filtering
The PNG format is interesting because it employs filtering at the scanline level.  This means it applies one of five possible functions to each scanline in order to increase the amount of order and, hence, improve the compression ratio.  For some reason, I got particularly stuck debugging this part.  The bugs, however, produced some interesting effects:

{{<showcase>}}
{{<img class="text-center" width="100%" src="/images/2012/medium-grayscale.png">}}
{{<img class="text-center" width="100%" src="/images/2012/img-nofiltering.png">}}
{{<img class="text-center" width="100%" src="/images/2012/img-second-bug.png">}}
{{</showcase>}}

On the left, we see the original image.  In the middle, we see the image produced by my decoder without filtering being applied.  On the right, we see the image produced by my decoder with a bug in the [Paeth Predictor](http://en.wikipedia.org/wiki/Portable_Network_Graphics#Filtering) filtering function.  The filters are based around adding the value of a previous pixel (e.g. to the left or above) to the current pixel.  As you can see on the right, one mistake early on in a scanline is then propagated along the scanline producing a variety of different effects!

## Constraints
Another interesting aspect of the PNG format are the constraints imposed on its data values.  The following excerpt from the spec defines constraints on the PNG header:
> The IHDR chunk shall be the first chunk in the PNG datastream. It contains:
> omitted tag "table"
> Width and height give the image dimensions in pixels. They are PNG four-byte unsigned integers. Zero is an invalid value.
> 
> Bit depth is a single-byte integer giving the number of bits per sample or per palette index (not per pixel). Valid values are 1, 2, 4, 8, and 16, although not all values are allowed for all colour types. See 6.1: Colour types and values.
> 
> Colour type is a single-byte integer that defines the PNG image type. Valid values are 0, 2, 3, 4, and 6.
> 
> Bit depth restrictions for each colour type are imposed to simplify implementations and to prohibit combinations that do not compress well.

These constraints are particularly interesting because, in Whiley, *I can represent them directly in the code*.  The following illustrates the PNG header abstraction:

```whiley
public define ValidColorDepths as [
 {1,2,4,8,16}, // GREYSCALE
 {},           // (empty)
 {8,16},       // TRUECOLOR
 ...
]

public define IHDR as { // Image Header
  u32 width,
  u32 height,
  BitDepth bitDepth,
  ColorType colorType,
  ...
} where width > 0 && height > 0 && bitDepth in ValidColorDepths[colorType]
```

Here, we can see the constraints on the `width`, `height` and `bitdepth` are encoded directly into the program.  Currently, violation of these constraints results in a runtime assertion failure.  In the future, the aim is to check them at compile time.
## Conclusion
It's been fun learning about the PNG and GIF file formats.  And, of course, writing realistic benchmarks provides extremely helpful feedback on the language design.  All up, I'm very pleased to see that writing programs in Whiley was an enjoyable experience!  There's a long way to go yet though, as writing these benchmarks has uncovered a bunch of compiler bugs for me to work on over the coming weeks and months ...
