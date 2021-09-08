/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

/**
 * Contains the list of areas that are tracked by Snippet. These will help getting the before/after
 * numbers while doing the PR review.
 */
public final class SnippetConstants {

    private SnippetConstants() {
        // private
    }

    // TTE stands for Time to Execute
    public static final String SUPER_ON_MAM_CREATE = "TTE super.onMamCreate()";
    public static final String INIT_SKYPE_DB_HELPER = "TTE SkypeTeamsDBHelper";
    public static final String INIT_BACKGROUND_OBJ = "TTE Initialize background objects";
    public static final String INIT_NOTIFICATION_CHANNEL_HELPER = "TTE Initialize notificationChannelHelper";
    public static final String INIT_NOTIFICATION_MGR = "TTE Initialize notification manager";
    public static final String SUPER_MA_ON_CREATE = "TTE super.onCreate() MA";
    public static final String INIT_SEARCH_BAR_VIEW = "TTE SearchBarView MA";
    public static final String SETUP_CONTENT_VIEW = "TTE SetupContentView";
    public static final String INJECT_DEPENDENCIES = "TTE injectIfNecessary";
    public static final String SETUP_TOOLBAR = "TTE setupToolBar";
    public static final String MA_LOAD_INACTIVE_TABS_UI_THREAD_WORK = "TTE loadInactiveTabs() UI thread Work";
    public static final String MA_LOAD_SELECTED_FRAGMENT = "TTE load selected fragment";
    public static final String DEPENDENCY_INJECTION = "TTE Dependency injection";
    public static final String BOTTOM_BAR_APP_CLICK_TELEMETRY = "Bottom Bar App Click Telemetry";
}
