package com.microsoft.sample.custom;

import android.util.Log;

import androidx.annotation.NonNull;

import com.microsoft.snippet.ExecutionContext;
import com.microsoft.snippet.Snippet;
import com.microsoft.snippet.token.ExtendableLogToken;
import com.microsoft.snippet.token.ILogToken;

/**
 * Demo for showing custom implementation of Execution Path. This path, takes the data that is
 * captured from the MeasuredExecutionPath and passes it to FileExecutionPath and then the data
 * is written to the file.
 * <p>
 * We need to override LogToken also, as for the code that is non contiguous all the measurements
 * are inside the log token that is handed over to the user by Snippet.startCapture() API
 * So if we want that our new execution to work for both kinds of APIs that ie.
 * The one passed through a lambda in Snippet.capture(lambda) and Snippet.startCapture()/LogToken.endCapture()
 * We need to override both the classes.
 */

public class FileExecutionPath extends Snippet.MeasuredExecutionPath {

    @Override
    public ILogToken startCapture(String tag) {
        return super.startCapture(tag);
    }

    @NonNull
    @Override
    public ExecutionContext capture(String message, Snippet.Closure closure) {
        ExecutionContext context = super.capture(message, closure);
        Log.d("Snippet", "Class: " + context.getClassName() + "Duration: " + context.getExecutionDuration());
        // Context has all the information that measured path has captured. Use that to write to files.
        return writeToFile(context);
    }

    private ExecutionContext writeToFile(ExecutionContext context) {
        // Code to write to a file goes here, create a thread and write.
        // Finally return a the execution context(could be the same or a new implementation) with some
        // of the details that you captured.

        // NOTE: always put the relevant information on the context before you start doing IO
        // so that the execution path could return successfully.
        return context;
    }

    @NonNull
    @Override
    public ExecutionContext capture(Snippet.Closure closure) {
        return super.capture(closure);
    }

    // We need to return a log token implementation that writes to a file when we call endCapture()
    // APIs.
    // USE ExtendableLogToken for the above purpose
    @Override
    public ILogToken startCapture() {
        return new ExtendableLogToken(super.startCapture());
    }

    @Override
    public ILogToken find(String tag) {
        return super.find(tag);
    }

    public class FileWritingLogToken extends ExtendableLogToken {

        public FileWritingLogToken(ILogToken logToken) {
            super(logToken);
        }

        @Override
        public ExecutionContext endCapture(String message) {
           ExecutionContext context = super.endCapture(message);
           writeToFile(context);
           return context;
        }

        @Override
        public ExecutionContext endCapture() {
            ExecutionContext context = super.endCapture();
            writeToFile(context);
            return context;
        }
    }
}
