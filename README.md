# Crash reproduction test task

You can find tests which reproduce given stack traces in the `test` directory.

## Lang crash

### Crash description

This crash happens because of a problem with era support in Japanese calendar in older versions of Java. As a result, while trying to work with Japanese eras, you could get an "index out of bounds" error.

My test sets the locale to use the Japanese calendar and simply calls the `getInstance()` method of `FastDateFormatter` with a pattern that uses eras. Error happens because of problem with eras indexation.

Small note: my test reproduces the same crash trace but with `Index out of bounds: 5`, not 4 as asked. It could be due to some issues with my local environment and Java version, so it's not always possible (or easy enough) to reproduce the exact same crash.

### About using ChatGPT

In this case, ChatGPT wasn't able to reproduce this crash just by the given crash trace and even some parts of the source code. But, it generated a pretty good "base" for the test, which eased debugging for me (since I didn't have to do anything to understand how to properly use `DateFormatter`, `setLocale`, etc.). Also, ChatGPT helped me create a test when I asked for adding usage of eras and the Japanese Calendar.

So, I had to debug it myself and realized I had to somehow manipulate with `Locale` to make something fail. ChatGPT helped me with information about supported calendar types and how to change them.

Also, after I found a test, I asked ChatGPT: "Do you know something about the bug 'index out of bounds' with Japanese eras in Java calendar in old lang3 libraries?" and it explained it to me!

```
As of my last knowledge update in September 2021, there was an issue related to Japanese eras in older versions of Java's java.util.Calendar and java.text.SimpleDateFormat classes, but this issue was not specific to the Apache Commons Lang 3 library. It was a known limitation in the Java platform itself, particularly in Java versions before Java 8.

The problem stemmed from the fact that older Java versions did not include support for Japanese eras introduced after the release of those Java versions. For example, the Java 6 and Java 7 versions did not have built-in support for the Reiwa era, which started in May 2019 in Japan.

As a result, if you were using these older Java versions and tried to work with Japanese eras, you might have encountered an "index out of bounds" or related issues when trying to format or parse dates with the Reiwa era or any other era introduced after the Java version's release.
```
So, if created test or full enough description is provided, LLM can be really useful!

## Math crash

### Crash description

This crash happens in `OpenMapRealVector.ebeMultiply()` method, if some modification to the elements of vector happens, while iterating through it. Using `setEntry()` method called in this function while iterating thrugh entries can cause such error, in case if entry wasn't set before, for example if we are setting this entry for the first time(only in this case `setEntry()` increases version counter which is checked in iterator)

So, my test just creates `OpenRealMapVector` and set some values in it. After that test call `ebeMultiply` method of this vector with not initalized `OpenMapRealVector`. This produces expected crash, because of adding new entries while iterating through vector. And I'm setting Locale to French to get exception in French language, as in statement.

### About using ChatGPT

In this task, after asking some questions of type "What this code about?", I was able to reproduce almost the whole test using ChatGPT. I've asked: `What do you know about the crash on Java 8 in an old version of Apache Commons Math? It giving me such stacktrace, but Im stuck while creating test for it. Can you help me? *crash trace*` and I've got working test!(alomost):
```
import org.apache.commons.math.linear.OpenMapRealVector;

public class ConcurrentModificationTest {
    public static void main(String[] args) {
        // Create an OpenMapRealVector
        OpenMapRealVector vector = new OpenMapRealVector();

        // Add some entries to the vector
        vector.setEntry(1, 10.0);
        vector.setEntry(2, 20.0);

        try {
            // Simulate concurrent modification by modifying the vector or the underlying map
            // You may need to experiment with different modifications here
            // vector.setEntry(3, 30.0); // Uncomment this line to simulate a modification
            // vector.getMapping().put(4, 40.0); // Uncomment this line to simulate a modification
            
            // Perform element-wise multiplication
            vector.ebeMultiply(new OpenMapRealVector()); // Simulate ebeMultiply
        } catch (Exception e) {
            // Handle the ConcurrentModificationException here
            e.printStackTrace();
        }
    }
}
```
I had to refactor it a bit to make code compile and it reproduced expected error, except that message was in english, not French(but it was easy to fix, just by asking chatgpt "how to change language of exception message?").

In this case, we can see that ChatGPT was not really sure if it works, it asked me to try modify some code to reproduce this bug. It wasn't able to really find the problem and test it, but offered some ways to try, which fortunatlly were succesfull.

## Summary

ChatGPT was really helpful for me for writing tests for the code I see first time, but it's not an ultimate solution: it can provide good test, approaches to try or even good explanation why crash (might) happens, but it should be really good questionsa and enough information provided to it and still it needs some people modifications upon it.


