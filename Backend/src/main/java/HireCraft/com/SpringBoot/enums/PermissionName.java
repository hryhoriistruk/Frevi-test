package HireCraft.com.SpringBoot.enums;

public enum PermissionName {
        //Admin Mgmt
        VIEW_ALL_USERS,
        MANAGE_USERS,
        APPROVE_DOCUMENTS,
        VIEW_ALL_REVIEWS,

        //User Mgmt
        VIEW_USER_PROFILE,
        EDIT_USER_PROFILE,
        DELETE_USER_ACCOUNT,
        SEND_MESSAGE,
        RECEIVE_MESSAGE,

        //Provider Permissions
        UPDATE_BOOKING_REQUEST_STATUS,
        UPLOAD_CV,
        UPLOAD_PORTFOLIO_IMAGES,
        RECEIVE_PAYMENT,
        WITHDRAW_PAYMENT,
        VIEW_EARNING_HISTORY,
        VIEW_PROVIDER_REVIEWS,
        VIEW_BOOKING_REQUEST_PROVIDER,

        //Client Permissions
        BOOK_SERVICE_PROVIDER,
        ADD_REVIEW,
        MAKE_PAYMENT,
        CANCEL_BOOKING_REQUEST,
        VIEW_CLIENT_REVIEWS,
        VIEW_PAYMENT_HISTORY,
        VIEW_BOOKING_REQUEST_CLIENT,

        //System Settings
        MANAGE_SETTINGS,
    }
