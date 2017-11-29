package saim.com.contactsync.Utility;

/**
 * Created by sam on 8/5/17.
 */

public class ApiURL {

    public static String header = "http://globalearnmoney.com/";

    public static String getLogin = header + "contact_sync/login.php"; //user_email, user_pass
    public static String getContactUpload = header + "contact_sync/contact_insert.php"; //user_id, contact_list
    public static String getContactList = header + "contact_sync/contact_retrive.php"; //user_id
}
