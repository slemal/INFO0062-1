# Audio filter

This is a school project in Java. It implements audio filters.

## Description

This library implements audio filters that can be used to filter `.wav` files.

## Using this library with Linux (terminal)

### Compiling it

Once in the directory `oop_lemal_simon`, the simplest way to compile the library is to run the
 following command:

```shell script
javac -d bin/ -cp audio.jar $(find -name *.java)
```

Pay attention that compiling only `src/*.java` will not work as this project contains packages.

### Executing the `Demo` script

Once you compiled `Demo`, you can run it with:

```shell script
java -cp bin/:audio.jar Demo Source.wav Filtered.wav
```
where `Source.wav` should be replaced by the name of your source file and `Filtered.wav` by the
 name the filtered file should have. If omitted, the destination file is going to be named
  `Filtered.wav` and put in the root directory.

The above command applies an echo filter to the source file. If you want to apply the
 large room reverberator instead, you can invoke it with this command:
 
```shell script
java -cp bin/:audio.jar Demo reverb Source.wav Filtered.wav
``` 

You can also replace the `reverb` argument by `jcrev` to apply the `JCRev` reverberator.

### Exceptions handling

This library should handle many exceptions caused by the user. I cannot claim to have handled all
 of them, but I hope I did not forget the most obvious ones.

If the user tries to create a filter
 that is completely invalid (e.g. contains unconnected inputs, loops with no delay, etc), an
  appropriate `FilterException` is going to be thrown.
  
If the filter is valid but contains some
 "useless" blocks, that is, blocks whose outputs are not connected to an output of the filter
  in any way, a warning message is going to be printed in `System.err` but the execution is not
   going to be stopped (even if these useless blocks create loops, or have empty inputs).
 
The file `ExceptionHandlingExample` provides examples of exceptions my library handles. Each
 commented out line causes an exception if uncommented and each line labelled `// Comment me out.`
  causes an exception if commented out. Lines labelled `// Causes a warning.` cause a warning.

## Project structure

### Main project files

This project's files are organised in packages. The `src/` directory contains all source files.
 The executable ones are located there (their usage is described above). Other files are in the
  package `be.uliege.lemal.oop.filters`.
  
The first subpackage, `elementaryFilters`, contains the source code of all basic filters
 (addition, delay, gain, etc).

The second subpackage, `compositeFilters`, contains the code of the `compositeFilter` class and
 of several of its child classes (all-pass, echo, etc). It also contains the package `blocks`
  which contains the source code of the classes used to implement composite filters, by
   simulating flow diagrams.

### Reverberator files

The following files, located in the package `be.uliege.lemal.oop.filters.compositeFilters`, where
 written in order to build the reverberator:
 
 * `AllPassFilter`
 * `LowPassFilter`
 * `Reverb`
 
Additionally, small changes where made to `AdditionFilter` in package 
`be.uliege.lemal.oop.filters.elementaryFilters` so that it can accept more than two inputs.

### JCRev files

I also had to write new files to implement the JCRev reverberator. As it was not asked in
 statement, I do not expect them to be read by the person who corrects this project. I only
  include them for the sake of completeness.
   
The files I added are mostly located in package `be.uliege.lemal.oop.filters.compositeFilters`.
 They are

 * `CombFilter`
 * `JCRev`
 
I also added a variant of the addition filter, the mean filter. Its source code can be found in
 the file `MeanFilter` in package `be.uliege.lemal.oop.filters.elementaryFilters`.
