package com.example.ui.strings

import com.example.data.model.Language

object Strings {
    fun get(lang: Language, key: String): String {
        val map = if (lang == Language.BANGLA) bnStrings else enStrings
        return map[key] ?: enStrings[key] ?: key
    }

    private val enStrings = mapOf(
        "app_title" to "X-Link LTD",
        "company_subtitle" to "ISP Customer Management & Billing",
        "role_customer" to "Customer View",
        "role_agent" to "Agent Portal",
        "status_active" to "Active",
        "status_expired" to "Expired / Due",
        "status_suspended" to "Suspended",
        "status_pending" to "Pending",

        // Navigation
        "nav_overview" to "Overview",
        "nav_bills" to "Bills & Payments",
        "nav_ai_support" to "Gemini AI",
        "nav_health" to "Network Health",
        "nav_new_conn" to "New Line",
        "nav_alerts" to "Alerts",
        "nav_agent_dashboard" to "Agent Dashboard",
        "nav_directory" to "Customers",
        "nav_collect" to "Bill Collection",
        "nav_requests" to "Installations",
        "nav_api_sync" to "Web API Sync",

        // Customer Dashboard
        "welcome_user" to "Welcome Back,",
        "connection_id" to "User ID",
        "ip_address" to "IP Address",
        "onu_mac" to "ONU MAC",
        "current_plan" to "Current Package",
        "monthly_charge" to "Monthly Fee",
        "due_amount" to "Outstanding Due",
        "due_date" to "Due Date",
        "pay_now" to "Pay Bill Now",
        "paid_status" to "Paid",
        "unpaid_status" to "Unpaid",
        "optical_power" to "Optical RX Power",
        "optical_healthy" to "Healthy (-18 to -24 dBm)",
        "live_speed" to "Real-time Bandwidth",
        "ping_latency" to "Latency",
        "package_speed" to "Fiber Speed",
        "recent_invoices" to "Recent Invoices",
        "view_all" to "View All",
        "view_receipt" to "View Receipt",

        // Payment
        "select_payment_method" to "Select Payment Method",
        "payment_title" to "Pay Monthly Internet Bill",
        "confirm_and_pay" to "Confirm & Pay ৳",
        "enter_mobile_pin" to "Enter MFS Wallet / Account Number",
        "enter_pin" to "Enter Security PIN / OTP",
        "payment_successful" to "Payment Successful!",
        "line_reactivated_msg" to "Your internet line has been automatically renewed & reactivated.",

        // New Connection
        "request_new_connection" to "Request New Fiber Connection",
        "new_connection_desc" to "Fast setup within 24 hours with optical fiber installation",
        "full_name" to "Full Name",
        "mobile_number" to "Mobile Number",
        "email_address" to "Email Address",
        "full_address" to "Installation Address (House/Road/Area)",
        "select_zone" to "Select Coverage Zone",
        "select_package" to "Choose Internet Package",
        "router_needed" to "Include Dual-Band Gigabit Wi-Fi Router",
        "submit_request" to "Submit Connection Request",
        "request_submitted_success" to "Request Submitted Successfully!",
        "track_request_status" to "Tracking Status",

        // Agent Dashboard
        "agent_dashboard_title" to "Field Agent & Billing Operations",
        "today_collection" to "Today's Collection",
        "total_active_subscribers" to "Active Subscribers",
        "pending_dues_count" to "Due Accounts",
        "pending_installs" to "Pending Installations",
        "quick_bill_collection" to "Instant Field Bill Collection",
        "search_customer_hint" to "Search by User ID, Mobile, or Name...",
        "collect_cash" to "Collect Cash & Activate",
        "print_receipt" to "Issue Digital Receipt",
        "change_status" to "Update Status",
        "grant_grace" to "Grant 3 Days Grace Period",

        // Web Billing API
        "api_integration_title" to "Web Billing Software API Integration",
        "api_subtitle" to "Real-time two-way synchronization with central billing ERP",
        "api_base_url" to "Billing API Endpoint URL",
        "api_key" to "Secret API Key",
        "bearer_token" to "Bearer Authorization Token",
        "sync_now" to "Sync Now with Web Server",
        "test_connection" to "Test API Ping",
        "sync_status" to "Sync Status",
        "realtime_sync_active" to "Real-Time Webhook Active",
        "live_sync_logs" to "API Request / Response Live Logs",
        "status_connected" to "Connected & In Sync",
        "status_offline" to "Offline Cache Active",

        // Notifications
        "notifications_title" to "Automated Alerts & Notifications",
        "mark_all_read" to "Mark All as Read",
        "no_notifications" to "No new alerts at this time"
    )

    private val bnStrings = mapOf(
        "app_title" to "এক্স-লিংক লিমিটেড",
        "company_subtitle" to "আইএসপি গ্রাহক ব্যবস্থাপনা ও বিলিং সফটওয়্যার",
        "role_customer" to "গ্রাহক মোড",
        "role_agent" to "এজেন্ট ড্যাশবোর্ড",
        "status_active" to "সক্রিয়",
        "status_expired" to "মেয়াদোত্তীর্ণ / বকেয়া",
        "status_suspended" to "স্থগিত",
        "status_pending" to "অপেক্ষমান",

        // Navigation
        "nav_overview" to "হোম",
        "nav_bills" to "বিল ও পেমেন্ট",
        "nav_ai_support" to "AI সাপোর্ট",
        "nav_health" to "নেটওয়ার্ক সিগন্যাল",
        "nav_new_conn" to "নতুন সংযোগ",
        "nav_alerts" to "নোটিফিকেশন",
        "nav_agent_dashboard" to "এজেন্ট ড্যাশবোর্ড",
        "nav_directory" to "গ্রাহক তালিকা",
        "nav_collect" to "বিল কালেকশন",
        "nav_requests" to "ইন্সটলেশন রিকোয়েস্ট",
        "nav_api_sync" to "ওয়েব API সিঙ্ক",

        // Customer Dashboard
        "welcome_user" to "স্বাগতম,",
        "connection_id" to "ইউজার আইডি",
        "ip_address" to "আইপি অ্যাড্রেস",
        "onu_mac" to "ONU ম্যাক",
        "current_plan" to "বর্তমান প্যাকেজ",
        "monthly_charge" to "মাসিক বিল",
        "due_amount" to "বর্তমান বকেয়া",
        "due_date" to "বিল পরিশোধের শেষ তারিখ",
        "pay_now" to "এখনই বিল পরিশোধ করুন",
        "paid_status" to "পরিশোধিত",
        "unpaid_status" to "অপরিশোধিত",
        "optical_power" to "অপটিক্যাল RX সিগন্যাল",
        "optical_healthy" to "উত্তম (-১৮ থেকে -২৪ dBm)",
        "live_speed" to "রিয়েল-টাইম ব্যান্ডউইথ",
        "ping_latency" to "লেটেন্সি / পিং",
        "package_speed" to "ফাইবার স্পিড",
        "recent_invoices" to "সাম্প্রতিক ইনভয়েস",
        "view_all" to "সব দেখুন",
        "view_receipt" to "মানি রিসিট দেখুন",

        // Payment
        "select_payment_method" to "পেমেন্ট মাধ্যম বেছে নিন",
        "payment_title" to "মাসিক ইন্টারনেট বিল পরিশোধ",
        "confirm_and_pay" to "বিল পরিশোধ করুন ৳",
        "enter_mobile_pin" to "মোবাইল ব্যাংকিং / ওয়ালেট নাম্বার",
        "enter_pin" to "সিকিউরিটি পিন / ওটিপি দিন",
        "payment_successful" to "বিল পরিশোধ সফল হয়েছে!",
        "line_reactivated_msg" to "আপনার ইন্টারনেট সংযোগটি স্বয়ংক্রিয়ভাবে রিনিউ ও চালু করা হয়েছে।",

        // New Connection
        "request_new_connection" to "নতুন ফাইবার সংযোগের আবেদন",
        "new_connection_desc" to "২৪ ঘণ্টার মধ্যে অপটিক্যাল ফাইবার সংযোগ স্থাপন",
        "full_name" to "গ্রাহকের পুরো নাম",
        "mobile_number" to "মোবাইল নাম্বার",
        "email_address" to "ইমেইল অ্যাড্রেস",
        "full_address" to "সংযোগের ঠিকানা (বাড়ি/রোড/এলাকা)",
        "select_zone" to "কভারেজ জোন নির্বাচন করুন",
        "select_package" to "ইন্টারনেট প্যাকেজ পছন্দ করুন",
        "router_needed" to "ডুয়েল-ব্যান্ড গিগাবিট ওয়াইফাই রাউটার প্রয়োজন",
        "submit_request" to "আবেদন জমা দিন",
        "request_submitted_success" to "সংযোগের আবেদনটি সফলভাবে গৃহীত হয়েছে!",
        "track_request_status" to "আবেদনের বর্তমান স্থিতি",

        // Agent Dashboard
        "agent_dashboard_title" to "ফিল্ড এজেন্ট ও কালেকশন ড্যাশবোর্ড",
        "today_collection" to "আজকের কালেকশন",
        "total_active_subscribers" to "সক্রিয় গ্রাহক",
        "pending_dues_count" to "বকেয়া গ্রাহক",
        "pending_installs" to "নতুন সংযোগ অপেক্ষমান",
        "quick_bill_collection" to "তাৎক্ষণিক ফিল্ড বিল কালেকশন",
        "search_customer_hint" to "ইউজার আইডি, মোবাইল বা নাম দিয়ে খুঁজুন...",
        "collect_cash" to "নগদ গ্রহণ ও লাইন চালু করুন",
        "print_receipt" to "ডিজিটাল রিসিট প্রদান",
        "change_status" to "স্ট্যাটাস পরিবর্তন",
        "grant_grace" to "৩ দিনের গ্রেস পিরিয়ড দিন",

        // Web Billing API
        "api_integration_title" to "ওয়েব বিলিং সফটওয়্যার API ইন্টিগ্রেশন",
        "api_subtitle" to "মূল বিলিং সার্ভার ERP এর সাথে রিয়েল-টাইম ডেটা সিঙ্ক্রোনাইজেশন",
        "api_base_url" to "বিলিং API এন্ডপয়েন্ট URL",
        "api_key" to "সিক্রেট API কী",
        "bearer_token" to "Bearer অথোরাইজেশন টোকেন",
        "sync_now" to "ওয়েব সার্ভারের সাথে সিঙ্ক করুন",
        "test_connection" to "API পিং টেস্ট করুন",
        "sync_status" to "সিঙ্ক স্থিতি",
        "realtime_sync_active" to "রিয়েল-টাইম ওয়েবহুক সক্রিয়",
        "live_sync_logs" to "API রিকোয়েস্ট ও রেসপন্স লাইভ লগ",
        "status_connected" to "সংযুক্ত ও সিঙ্কড",
        "status_offline" to "অফলাইন ক্যাশ সক্রিয়",

        // Notifications
        "notifications_title" to "স্বয়ংক্রিয় নোটিফিকেশন ও অ্যালার্ট",
        "mark_all_read" to "সবগুলো পঠিত হিসেবে চিহ্নিত করুন",
        "no_notifications" to "এই মুহূর্তে নতুন কোনো নোটিফিকেশন নেই"
    )
}
