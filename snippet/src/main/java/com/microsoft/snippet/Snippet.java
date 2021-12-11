/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.snippet.token.AttenuatedLogToken;
import com.microsoft.snippet.token.ExtendableLogToken;
import com.microsoft.snippet.token.ILogToken;
import com.microsoft.snippet.token.LogTokenState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Snippet is a small library which can be used for measuring the time taken to execute a code
 * snippet. This library intends to reduce the boiler place code needed to add the start and end timestamps,
 * adding the execution context such as class,method and line number or any other relevant data and
 * printing the logs. The work is tedious and it becomes a nightmare when we have to do it across the
 * code base. The API provided by this library could be used to monitor PRs in the PR reviews where
 * the reviewer could ask for numbers for a specific snippet and compare the before and after numbers.
 * It could also be used as a general library to print code execution duration.
 * <p>
 * The important thing to note is we can configure the code in such a way that we do not have to
 * care about the additional verbosity in the release builds as it automatically makes the code
 * no-op in the release builds or any other builds if we want. We also have an option to configure the library
 * differently in different builds. Please check {@link Snippet#install(ExecutionPath)} API.
 *
 * <h2> There are 2 ways to measure time in Snippet</h2>
 * <ol>
 *     <li> Using <code>capture</code> APIs of Snippet</li>
 *     <li> Using log tokens and <code>startCapture</code> and <code>endCapture</code> APIs </li>
 * </ol>
 * <p>
 * Snippet's capture() API accepts a lambda and measures the time it takes to execute it and logs it
 * on the log cat.
 * Example usage with <code>Snippet.capture</code>:
 * <pre>
 * {@code
 *     Snippet.capture(() -> {
 *             final ActionBar actionBar = getSupportActionBar();
 *             if (actionBar != null && actionBar.isShowing()) {
 *                 if (mToolbar != null) {
 *                     ViewCompat.setElevation((View) mToolbar.getParent(), 0F);
 *                     mToolbar.animate()
 *                             .translationY(-mToolbar.getHeight())
 *                             .setInterpolator(new AccelerateDecelerateInterpolator())
 *                             .withEndAction(new Runnable() {
 *                                 {@literal}Override
 *                                 public void run() {
 *                                     actionBar.hide();
 *                                     if (mToolbarTranslateYListener != null) {
 *                                         mToolbarTranslateYListener.onAnimationFinished();
 *                                     }
 *                                 }
 *                             })
 *                             .start();
 *                 } else {
 *                     actionBar.hide();
 *                 }
 *             }
 *         });
 * }
 * </pre>
 * Here library is going to measure time used to execute the code represented by the underlying lambda.
 * <p>
 * When you intent to measure the execution times for code which is not spread across multiple
 * methods and classes, capture() API can be used. There is one caveat though, while capturing, we pass
 * a lambda for the closure representing the code snippet, this might be a problem if we want to capture
 * some non final variables outside the scope of lambda(Java does not support that). For that use, {@link Final} to create a
 * wrapper around your variable and use {@link Final#get()} and {@link Final#set(Object)}()} methods.
 * This is tedious though, but if you want to use the capture based approach this is the way out. The new
 * approach for this use case is described in the next section.
 * <p>
 * Another approach is through {@link Snippet#startCapture()} & {@link LogToken#endCapture()} APIs.
 * <code>startCapture</code> is going to return a token representing your execution it can be passed
 * from one component to another. The moment you call <code>endCapture()</code> it will measure the
 * time and print the logs.
 * <p>
 * Example usage of <code>Snippet.startCapture()</code> and <code>LogToken.endCapture()</code>:
 * <pre>
 *     {@code
 *      Snippet.LogToken token = Snippet.startCapture();        // start the measurement
 *         getEditText().setOnTouchListener((v, event) -> {
 *             mIsQueryTextFromVoiceInput = false;
 *             if (!TextUtils.isEmpty(getEditText().getText())) {
 *                 updateClearIconAndVoiceEntryVisibility(VISIBLE, GONE);
 *             }
 *
 *             if (mOnEditTextTouchListener != null) {
 *                 return mOnEditTextTouchListener.onTouch(v, event);
 *             }
 *
 *             return false;
 *         });
 *
 *         initVoiceInputEntry(context);
 *         token.endCapture();  // end measurement and print logs.
 *     }
 * </pre>
 * We can use <code>startCapture()</code> method with a TAG also. This is particularly useful when
 * the execution is spread across multiple classes. We can use, {@link Snippet#find(String)} to find
 * the log token that was created with this tag. The log token received can be then used normally.
 * {@link LogToken} objects are internally obtained through a pool so, Snippet tries it best to
 * recycle the objects again and again.
 * <p>
 * There is concept of filter, which can be used to filter the logcat output. You can set the filter
 * using {@link Snippet#newFilter(String)}. Default filter is <b>Snippet</b>
 * While this is global filter for all the logs, you can still choose to have a different filter for a
 * particular LogToken using {@link LogToken#overrideFilter(String)} which will override the global filter.
 * <p>
 * Snippet can print class, method and line number information in the logs as a part of execution
 * context. By default it prints class and method name. You can choose to have your combination of
 * details through {@link Snippet#addFlag(int)}
 * <b>Valid options are:</b>
 * <ol>
 *
 *     <li>{@link Snippet#FLAG_METADATA_CLASS}</li>
 *
 *     <li>{@link Snippet#FLAG_METADATA_METHOD}</li>
 *
 *     <li>{@link Snippet#FLAG_METADATA_LINE}</li>
 *
 *     <li>{@link Snippet#FLAG_NONE}</li>
 * </ol>
 * <p>
 * While these information should be enough but if you need to add some more information to the logs
 * like variable values, states etc then use {@link Snippet#capture(String, Closure)} and
 * {@link LogToken#endCapture(String)} overloads for adding custom messages to the logcat
 * output.
 * <p>
 *     Snippet also supports the concept of <b>ThreadLocks</b>. ThreadLocks is a feature where a log token
 *     can declare the thread creating the log token would only be able to call endCapture(). If other
 *     thread tries to call endCapture(). It will log an error. To enable thread locks one should
 *     call {@link LogToken#enableThreadLock()} and to check if the ThreadLock is enabled or not
 *     {@link LogToken#isThreadLockEnabled()} can be used.
 * </p>
 *
 * <p> There is a interesting concept of splits:
 *     There are times while you are measuring a sequence of code and you would like to measure how
 *     much time some steps take within that sequence. As an example, while you are measuring some
 *     method that takes 300ms to execute, it could have multiple areas inside it that could be adding up
 *     to that number. So, to measure that {@link LogToken#addSplit()} and {@link LogToken#addSplit(String)}
 *     could be used. Each call to add split print the time takes since the last addSplit() was called.
 *     If addSplit() is called for the first time, then it would measure the time from startCapture().
 *     Once the endCapture() is called and there are {@link Split} inside your capture, snippet also prints
 *     a clean split summary that shows what was the fraction of time each split took to give an overall idea to
 *     the user.
 * </p>
 *
 * <p>
 *     Snippet also introduces a concept of {@link ExecutionPath}. It is a routing mechanism where, it
 *     tell the library the way to route its code. Whenever an Snippet API is called, it relays it to an execution path.
 *     That directs it to the core library functionality. So, this provides a capability where the user can
 *     provide custom execution path implementations and make Snippet run their own code. It also provides a
 *     mechanism to run different execution paths in different build types. Just add a check for your build type and
 *     install the execution path that you want using {@link Snippet#install(ExecutionPath)} method.
 * <p>
 *     If your aim is to do the measurement then {@link MeasuredExecutionPath} is already shipped with Snippet.
 *     Any additional work could be added by extending {@link MeasuredExecutionPath}.
 *
 *    <b> Steps to implement a custom path. See FileExecutionPath in the github sample app for a demo.</b>
 *     1. Extend ExecutionPath, in our example we will extend MeasuredExecutionPath.
 *     2. Override {@link ExecutionPath#capture(Closure)} and {@link ExecutionPath#capture(String, Closure)}
 *     This will make sure that you are implementing a custom code for lambda based API.
 *     3. There are non-contiguous areas of code also, to address that we have {@link Snippet#startCapture()}
 *     {@link Snippet#startCapture(String)} APIs, that returns a log token, so if you are writing a new execution path
 *     you need to provide a custom log token also and the {@link LogToken#endCapture()} and {@link LogToken#endCapture(String)}
 *     so that you can perform the custom actions on all types of code. For doing this extend
 *     {@link com.microsoft.snippet.token.ExtendableLogToken} and override
 *     {@link com.microsoft.snippet.token.ExtendableLogToken#endCapture(String)}, and
 *     {@link ExtendableLogToken#endCapture()}. Once done, return the ExtendableLogToken instance from
 *     {@link Snippet#startCapture(String)}, and {@link Snippet#startCapture(String)} methods.
 *
 *     NOTE: In almost all the cases, every new execution path that would be created, would require a new
 *     extension of {@link ExtendableLogToken}
 * </p>
 *
 * @author vishalratna
 */
public final class Snippet {

    public static final AttenuatedLogToken NO_OP_TOKEN = new AttenuatedLogToken();
    private static final String TAG = LogToken.class.getSimpleName();
    private static final ExecutionContext EMPTY_CONTEXT = new ExecutionContext();
    public static final int FLAG_METADATA_CLASS = 1 << 31;
    public static final int FLAG_METADATA_METHOD = 1 << 30;
    public static final int FLAG_METADATA_LINE = 1 << 29;
    public static final int FLAG_METADATA_THREAD_INFO = 1 << 28;
    public static final int FLAG_NONE = 0;

    private static String primaryFilter = Snippet.class.getSimpleName();
    private static final LogTokenPool OBJECT_POOL;
    private static String packageNameFilter = "com.microsoft";
    private static StackAnalyser stackAnalyser = new StackAnalyser(packageNameFilter);
    private static final TagHelper TAG_HELPER;
    private static int mFlags = FLAG_METADATA_CLASS | FLAG_METADATA_METHOD;
    private static final String SEPARATOR = "|::::|";
    private static final OneShot<Boolean> SHOULD_PRINT_DEBUG_LOGS = new OneShot<>(false);
    private static final OneShot<ExecutionPath> EXECUTION_PATH = new OneShot<ExecutionPath>(new ReleaseExecutionPath());  // Release is the default execution path
    static boolean mPrintDebugLogs = unBox(SHOULD_PRINT_DEBUG_LOGS.get());  // Do not set the value from anywhere other than turnOn/Off logs. Just meant for easy reference.

    static {
        TAG_HELPER = new TagHelper();
        OBJECT_POOL = new LogTokenPool();
    }

    private Snippet() {
    }

    /**
     * Installs custom execution path. It is best to call this as early as possible.
     * Prior to this call, core functionality will be routed to Release Execution path which is
     * default. Can be set once. Attempts to set it multiple times will not be honoured.
     *
     * @param path execution path.
     */
    public static void install(ExecutionPath path) {
        EXECUTION_PATH.set(path);
    }

    /**
     * Captures a closure which needs to be measured.
     *
     * @param message Custom message if any.
     * @param closure Lambda or implementation representing the closure.
     */
    public static void capture(String message, Closure closure) {
        EXECUTION_PATH.get().capture(message, closure);
    }

    /**
     * Captures a closure which needs to be measured when no custom messages are required.
     *
     * @param closure Lambda or implementation representing the closure.
     */
    public static void capture(Closure closure) {
        EXECUTION_PATH.get().capture(null, closure);
    }

    /**
     * Snippet identifies the execution context by analysing the stack frames and examining private
     * members of {@link StackTraceElement} class. Out of dozens of stack frames containing JDK
     * and android SDK frames, it has to identify the last frame which was related to the user application.
     * For that uses the regex matching this filter to identify the user's application frame.
     * After receiving the new regex it replaces the old stack analyser component with the new one.
     * This impacts stack analyser of the log token also. Any log token created after this call will
     * use the new regex, but token created before this call will have this one.
     *
     * @param regex REGEX identifying the application.
     */
    public static void setPackageRegex(String regex) {
        packageNameFilter = regex;
        stackAnalyser = new StackAnalyser(packageNameFilter);
    }

    private static ExecutionContext invokeMeasureAndAttachExecutionContext(String message, Closure closure) {
        long delta = ToolBox.invokeAndMeasure(closure);
        ExecutionContext executionContext = getExecutionContext();
        executionContext.setExecutionDuration(delta);

        // Build the log string using the snippet info we got.
        StringBuilder logMessageBuilder = new StringBuilder();
        if (message != null && !message.isEmpty()) {
            logMessageBuilder.append(message).append("::");
        }

        appendExecutionContextToLog(logMessageBuilder, executionContext);

        logMessageBuilder.append(SEPARATOR).append('(').append(delta).append(" ms)");
        Log.d(primaryFilter, logMessageBuilder.toString());
        return executionContext;
    }

    /**
     * Returns the execution context in te form of SnippetInfo class. That can be returned and
     * used by external clients too.
     *
     * @return snippet info.
     */
    private static ExecutionContext getExecutionContext() {

        Thread thread = Thread.currentThread();

        ExecutionContext info = new ExecutionContext();
        info.setClassName(stackAnalyser.callingClass(thread, StackAnalyser.API_CAPTURE));
        info.setMethod(stackAnalyser.callingMethod(thread, StackAnalyser.API_CAPTURE));
        info.setLineNo(stackAnalyser.callingLine(thread, StackAnalyser.API_CAPTURE));
        info.setThreadName(thread.getName());

        return info;
    }

    private static void appendExecutionContextToLog(StringBuilder logMessageBuilder, ExecutionContext context) {
        if (testFlag(FLAG_METADATA_CLASS)) {
            if (Snippet.mPrintDebugLogs) {
                Log.d(TAG, "FLAG_METADATA_CLASS set");
            }
            String trimmedClass = trimPackageFromClass(context.getClassName());
            logMessageBuilder.append("[Class = ").append(trimmedClass).append(']').append(SEPARATOR);
        }
        if (testFlag(FLAG_METADATA_METHOD)) {
            if (Snippet.mPrintDebugLogs) {
                Log.d(TAG, "FLAG_METADATA_METHOD set");
            }
            logMessageBuilder.append("[Method = ").append(context.getMethodName()).append(']').append(SEPARATOR);
        }
        if (testFlag(FLAG_METADATA_LINE)) {
            if (Snippet.mPrintDebugLogs) {
                Log.d(TAG, "FLAG_METADATA_LINE set");
            }
            logMessageBuilder.append("<Line no. ").append(context.getLineNo()).append('>').append(SEPARATOR);
        }
        if (testFlag(FLAG_METADATA_THREAD_INFO)) {
            if (Snippet.mPrintDebugLogs) {
                Log.d(TAG, "FLAG_METADATA_THREAD_INFO set");
            }
            logMessageBuilder.append("[Thread name = ").append(Thread.currentThread().getName()).append(']').append(SEPARATOR);
        }

    }

    private static String trimPackageFromClass(String qualifiedName) {
        String[] tokens = qualifiedName.split("\\.");
        if (Snippet.mPrintDebugLogs) {
            StringBuilder temp = new StringBuilder();
            for (String a : tokens) {
                temp.append('[').append(a).append("] ");
            }
            Log.d(TAG, "trimPackageFromClass() tokens: " + temp.toString());
        }
        return tokens[tokens.length - 1];
    }


    /**
     * Set a new global filter and returns the old filter, just in case if you need to restore it.
     *
     * @param newFilterKey new filter for logcat
     * @return old filter value.
     */
    public static String newFilter(String newFilterKey) {
        String oldTag = primaryFilter;
        primaryFilter = newFilterKey;
        return oldTag;
    }

    /**
     * Adds a flag which determines the execution context shown on the logs such as class name, method
     * and line no getting called on the logcat output. Supports
     * <ol>
     *  *     <li>{@link Snippet#FLAG_METADATA_CLASS}</li>
     *  *     <li>{@link Snippet#FLAG_METADATA_METHOD}</li>
     *  *     <li>{@link Snippet#FLAG_METADATA_LINE}</li>
     *  *     <li>{@link Snippet#FLAG_NONE}</li>
     *  * </ol>
     *
     * @param flag Integer representing the flags
     * @return new flag value.
     */
    public static int addFlag(int flag) {
        //assureCorrectFlag(flag);  // This will prevent us from supplying compound flags.
        mFlags |= flag;
        return mFlags;
    }

    private static void assureCorrectFlag(int flag) {
        if (flag != 1 << 31 && flag != 1 << 30 && flag != 1 << 29 && flag != 1 << 28) {
            throw new IllegalArgumentException("Please set a valid flag");
        }
        if (Snippet.mPrintDebugLogs) {
            Log.d(TAG, "Flag validation completed.");
        }
    }

    /**
     * Checks whether a particular flag is set in the Snippet or not.
     *
     * @param flag Flag to test
     * @return true if set, false otherwise.
     */
    public static boolean testFlag(int flag) {
        assureCorrectFlag(flag);
        return (mFlags & flag) == flag;
    }

    /**
     * Clears the flag which shows execution context.
     * Calling this will not show any execution context in the logs.
     */
    public static void clearFlags() {
        mFlags = FLAG_NONE;
    }

    /**
     * Starts the measurement of code spread across multiple classes and methods. It returns a
     * LogToken which can be passed across class and methods. To end the measurement call {@link LogToken#endCapture()}
     *
     * @return LogToken
     */
    @NonNull
    public static ILogToken startCapture() {
        return EXECUTION_PATH.get().startCapture();
    }


    /**
     * Starts the measurement of code spread across multiple classes and methods. It returns a
     * LogToken which can be passed as well as retrieved using a tag which is provided at the
     * time of token creation. It enables a direct access for the log token anywhere in the code.
     * If a token with the provided tag already exists, this call with return null which will make
     * further calls to {@link LogToken#endCapture()} crash.
     * If multiple threads simultaneously try to call this method with same tag, the first one to acquire the tag
     * wins and the other gets Pair object with supplied log token and a successFlag as false .
     * The token acquired in the initial step of this method is returned to the pool.
     *
     * @param tag tag
     * @return LogToken which can we retrieved using the tag if request successful, null otherwise
     */
    @Nullable
    public static ILogToken startCapture(String tag) {
        return EXECUTION_PATH.get().startCapture(tag);
    }

    /**
     * Finds a token with provided tag. The tag should match the tag provided with
     * {@link Snippet#startCapture(String)} or else Attenuated token is returned.
     *
     * @param tag Tag
     * @return LogToken if existing, attenuated token otherwise.
     */
    public static ILogToken find(String tag) {
        return EXECUTION_PATH.get().find(tag);
    }

    public static void turnOnLogging() {
        SHOULD_PRINT_DEBUG_LOGS.set(true);
        mPrintDebugLogs = unBox(SHOULD_PRINT_DEBUG_LOGS.get());
    }

    public static void turnOffLogging() {
        SHOULD_PRINT_DEBUG_LOGS.set(false);
        mPrintDebugLogs = unBox(SHOULD_PRINT_DEBUG_LOGS.get());
    }

    private static boolean unBox(Boolean aBoolean) {
        if (aBoolean == null) {
            return false;
        }
        return aBoolean;
    }

    /**
     * Piece of code whose execution times need to be measured.
     */
    public interface Closure {
        /**
         * Invokes the code which is passed inside the closure.
         * Users are not supposed to call this method directly.
         */
        void invoke();
    }

    /**
     * When the execution spreads across multiple classes and methods, and lambda cannot be used
     * to capture the code, then LogToken is created by calling {@link Snippet#startCapture()}, the
     * measurement start from this point, now we can pass the token anywhere
     * and, will end the measurement when {@link LogToken#endCapture()} is called.
     * Default filter for a log token is the global filter of Snippet.
     */
    public static class LogToken implements ILogToken {
        private static final String TAG = LogToken.class.getSimpleName();
        private static final String SPLIT_MESSAGE = "********SPLIT[" + "%1s" + "]"
                + SEPARATOR + "(" + "%2s" + " " + "ms"
                + ")" + "********";

        private final StackAnalyser mLocalAnalyser = new StackAnalyser(packageNameFilter);
        private long mStartTime;
        private long mEndTime;
        private String mFilter;
        private long mThreadId = -1L;
        private boolean mThreadLockEnabled = false;
        private volatile LogTokenState mState;
        private long mLastSplitTimeCaptured = 0L;
        private List<Split> mSplitRecord;

        // To be called only through LogTokenPool. Should not be created through any other ways.
        protected LogToken() {
            if (mPrintDebugLogs) {
                if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                    Log.d(TAG, "Inside LogToken's <init>. startCapture() called on the main thread. LogToken[" + this.toString() + "]");
                } else {
                    Log.d(TAG, "Inside LogToken's <init>. startCapture() called off the main thread. LogToken[" + this.toString() + "]");
                }
                Log.d(TAG, "<init> will be called once per LogToken object, after the usage is over, it will be returned to the pool and will get recycled using obtain()");
            }
            this.mStartTime = ToolBox.currentTime();
            this.mFilter = Snippet.primaryFilter;   // Uses the primary filter by default.
        }

        /**
         * Can be used to override the Global filter provided by Snippet.
         * Once this LogToken's endCapture() is called, the filter is reset to
         * the global filter again.
         *
         * @param newFilter new filter
         * @return LogToken instance on which start()/end() can be called.
         */
        @Override
        public LogToken overrideFilter(String newFilter) {
            this.mFilter = newFilter;
            return this;
        }

        /**
         * Returns current filter
         *
         * @return existing filter
         */
        @Override
        public String filter() {
            return this.mFilter;
        }

        @Override
        public long creatorThreadId() {
            return this.mThreadId;
        }

        @Override
        public boolean isThreadLockEnabled() {
            return mThreadLockEnabled;
        }

        @Override
        public ILogToken enableThreadLock() {
            mThreadLockEnabled = true;
            return this;
        }

        public void setCreatorThreadId(long id) {
            this.mThreadId = id;
        }

        @Override
        public void reset() {
            this.mStartTime = 0;
            this.mEndTime = 0;
            this.mFilter = Snippet.primaryFilter;
            this.mThreadId = -1L;
            this.mThreadLockEnabled = false;
            if (this.mSplitRecord != null) {
                this.mSplitRecord.clear();
            }
            this.mSplitRecord = null;
        }

        /**
         * Creates a split within the span of LogToken's startCapture() and endCapture() methods.
         * Calling addSplit() will measure the time taken since the last time addSplit() was called.
         * If addSplit() is called for the first time then it would measure the time between
         * {@link Snippet#startCapture()} and {@link LogToken#endCapture()}. Calling addSplit() after
         * endCapture() is called would lead to IllegalStateException.
         */
        @Override
        public void addSplit() {
            Split newSplit = addSplitInternal();
            Log.d(mFilter, String.format(SPLIT_MESSAGE, newSplit.sequence(), newSplit.delta()));
        }

        /**
         * Does the same thing as {@link LogToken#addSplit()}. It just additionally adds a message string
         * with the Split.
         *
         * @param message Custom message to print on the log cat.
         */
        @Override
        public void addSplit(String message) {
            Split newSplit = addSplitInternal();
            newSplit.setName(message);
            Log.d(mFilter, String.format(SPLIT_MESSAGE, "[" + newSplit.sequence() + "]" + message, newSplit.delta()));
        }

        private Split addSplitInternal() {
            // This is not fully true but a quick hack, will have to think about multiple states possible.
            if (mState != LogTokenState.ACTIVE) {
                throw new IllegalStateException("addSplit() called after endCapture() is executed! Development error!!!!!!");
            }
            Split newSplit = createSplit();
            synchronized (this) {
                if (mSplitRecord == null) {
                    mSplitRecord = new ArrayList<>();
                }
                mSplitRecord.add(newSplit);
            }
            return newSplit;
        }

        private Split createSplit() {
            Split newSplit;
            synchronized (this) {
                if (mState != LogTokenState.ACTIVE) {
                    throw new IllegalStateException("addSplit() called after endCapture() is executed! Development error!!!!!!");
                }
                if (mLastSplitTimeCaptured == 0L) {  // Split called for the first time
                    // We use the token start time as the reference.
                    mLastSplitTimeCaptured = ToolBox.currentTime();
                    newSplit = new Split(getStart(), mLastSplitTimeCaptured);
                } else {
                    // Here we use the last split time captured.
                    long currentTime = ToolBox.currentTime();
                    newSplit = new Split(mLastSplitTimeCaptured, currentTime);
                    mLastSplitTimeCaptured = currentTime;
                }
            }
            return newSplit;
        }

        @Override
        public void setState(LogTokenState state) {
            mState = state;
        }

        @Override
        public LogTokenState getState() {
            return mState;
        }

        /**
         * Ends the capture which was started through {@link Snippet#startCapture()}.
         */
        @Override
        public ExecutionContext endCapture() {
            synchronized (this) {
                if (mState == LogTokenState.END_CAPTURE_EXECUTED) {
                    return Snippet.EMPTY_CONTEXT;
                }
                ExecutionContext executionContext;
                if (ToolBox.willThreadLockGuardThisCapture(Thread.currentThread(), this)) {
                    Log.e(TAG, "ThreadLocks enabled! Not able to end the capture as the token "
                            + "creating thread is not same as the thread calling endCapture().");

                    return Snippet.EMPTY_CONTEXT;
                } else {
                    mState = LogTokenState.END_CAPTURE_EXECUTED;
                    executionContext = doEndSlice(null);
                    if (mSplitRecord != null && mSplitRecord.size() > 0) {
                        dumpSplitData(mSplitRecord, executionContext);
                    }
                    ILogToken token = TAG_HELPER.unTag(this);
                    if (token == null) {
                        Log.e(TAG, "Not able to unTag as the tag for the request was not available.");
                    }
                    OBJECT_POOL.recycle(this);
                    return executionContext;
                }
            }
        }

        private void dumpSplitData(List<Split> splits, ExecutionContext context) {
            StringBuilder recordSummaryBuilder = new StringBuilder('\n');
            recordSummaryBuilder.append("                  Split Summary").append('\n');
            recordSummaryBuilder.append("                  *********************************************************************************************"
                    + "*************************************");
            for (Split split : splits) {
                recordSummaryBuilder.append('\n').append('|').append("___").append("Split[").append(split.sequence()).append(']');
                if (split.getName() != null && !TextUtils.isEmpty(split.getName())) {
                    recordSummaryBuilder.append('[').append(split.getName()).append(']').append(' ');
                }
                recordSummaryBuilder.append(split.delta()).append('/').append(context.getExecutionDuration()).append(" ( ms ) ");
                recordSummaryBuilder.append("  ").append('(').append(String.format(Locale.US, "%.3f", split.percentage(context.getExecutionDuration()))).append(" %").append(')');
                recordSummaryBuilder.append(" of total capture.");
            }
            recordSummaryBuilder.append("\n                 *************************************************************************************************************************************");
            Log.d(mFilter, recordSummaryBuilder.toString());
        }

        /**
         * Ends the capture which was started through {@link Snippet#startCapture()}.
         *
         * @param message Custom message if required
         */
        @Override
        public ExecutionContext endCapture(String message) {
            synchronized (this) {
                if (mState == LogTokenState.END_CAPTURE_EXECUTED) {
                    return Snippet.EMPTY_CONTEXT;
                }
                ExecutionContext executionContext;
                if (ToolBox.willThreadLockGuardThisCapture(Thread.currentThread(), this)) {
                    Log.e(TAG, mFilter + " ThreadLocks enabled! Not able to end the capture as the"
                            + " token creating thread is not same as the thread calling endCapture(message).");

                    return Snippet.EMPTY_CONTEXT;
                } else {
                    mState = LogTokenState.END_CAPTURE_EXECUTED;
                    executionContext = doEndSlice(message);
                    if (mSplitRecord != null && mSplitRecord.size() > 0) {
                        dumpSplitData(mSplitRecord, executionContext);
                    }
                    ILogToken token = TAG_HELPER.unTag(this);
                    if (token == null) {
                        Log.d(TAG, "Not able to unTag as the tag for the request was not available.");
                    }
                    OBJECT_POOL.recycle(this);
                    return executionContext;
                }
            }
        }

        private ExecutionContext doEndSlice(String message) {
            synchronized (this) {
                mEndTime = ToolBox.currentTime();
                long delta = mEndTime - mStartTime;

                StringBuilder logMessageBuilder = new StringBuilder();
                if (message != null && !message.isEmpty()) {
                    logMessageBuilder.append(message).append(SEPARATOR);
                }
                Thread thread = Thread.currentThread();
                if (mPrintDebugLogs) {
                    if (thread == Looper.getMainLooper().getThread()) {
                        Log.d(TAG, "endCapture() called on the main thread. LogToken[" + this.toString() + "]");
                    } else {
                        Log.d(TAG, "endCapture() called off the main thread");
                    }
                }
                ExecutionContext executionContext = new ExecutionContext();
                executionContext.setClassName(mLocalAnalyser.callingClass(thread, StackAnalyser.API_LOG_TOKEN));
                executionContext.setMethod(mLocalAnalyser.callingMethod(thread, StackAnalyser.API_LOG_TOKEN));
                executionContext.setLineNo(mLocalAnalyser.callingLine(thread, StackAnalyser.API_LOG_TOKEN));
                executionContext.setThreadName(thread.getName());
                executionContext.setExecutionDuration(delta);

                appendExecutionContextToLog(logMessageBuilder, executionContext);
                logMessageBuilder.append(SEPARATOR).append('(').append(delta).append(" ms)");
                Log.d(mFilter, logMessageBuilder.toString());
                return executionContext;
            }
        }

        @Override
        public final long getStart() {
            return mStartTime;
        }

        @Override
        public final long getEnd() {
            return mEndTime;
        }

        @Override
        public final void setStart(long start) {
            this.mStartTime = start;
        }

        @Override
        public final void setEnd(long start) {
            this.mStartTime = start;
        }
    }


    /**
     * Execution path that is used by Snippet to route the code to the core library functionality.
     */
    public static class MeasuredExecutionPath implements ExecutionPath {

        @Override
        @NonNull
        public ExecutionContext capture(String message, Closure closure) {
            return invokeMeasureAndAttachExecutionContext(message, closure);
        }

        @Override
        @NonNull
        public ExecutionContext capture(Closure closure) {
            return invokeMeasureAndAttachExecutionContext(null, closure);
        }

        @Override
        public ILogToken startCapture() {
            long startTime = ToolBox.currentTime();
            ILogToken token = OBJECT_POOL.obtain();
            token.setStart(startTime);
            token.setCreatorThreadId(Thread.currentThread().getId());
            return token;
        }

        @Override
        public ILogToken startCapture(String tag) {
            long startTime = ToolBox.currentTime();
            ILogToken token = OBJECT_POOL.obtain();
            token.setStart(startTime);
            token.setCreatorThreadId(Thread.currentThread().getId());
            Pair<ILogToken, Boolean> tagResult = TAG_HELPER.tag(tag, token);
            if (!tagResult.getSecond()) {
                Log.e(TAG, "Tag: [" + tag + "] already exists in the record, cannot assign log token, so we are providing a NO_OP_TOKEN.");
                OBJECT_POOL.recycle(token);
                return NO_OP_TOKEN;
            }
            if (mPrintDebugLogs) {
                Log.e(TAG, "Tag: [" + tag + "] created for the LogToken.");
            }
            return token;
        }

        /**
         * Used to find Log Token that was created using {@link Snippet#startCapture(String)}
         * If we try to find a tag that does not exist it would return {@link Snippet#NO_OP_TOKEN}
         * that is a NO-OP implementation of {@link ILogToken} interface.
         *
         * @param tag Custom tag. This tag will be used to search the log token across the app.
         * @return LogToken if existing with the tag, No op token otherwise.
         */
        @Override
        public ILogToken find(String tag) {
            ILogToken token = TAG_HELPER.search(tag);
            if (token == null) {
                return NO_OP_TOKEN;
            } else {
                return token;
            }
        }
    }
}
