[["java:package:com.qingzhenyun.common.ice"]]
module usercenter{
    exception RegisterFailedException{
        int innerCode;
        string innerMessage;
    };
    exception LoginFailedException{
            int innerCode;
            string innerMessage;
        };
    //["java:type:java.util.HashMap<String,String>"]
    dictionary<string, string> SsidMap;
    class UserDataResponse{
        long  uuid;
        string  name;
            // string  password = 2;
            // private final String  salt;
        string  email;
        string  countryCode;
        string  phone;
        long    createTime;
            // private final String  createIp;
        string  createIp;
        SsidMap ssid;
        string icon;
        long spaceUsed;
        long spaceCapacity;
        int type;
        int status;
        int version;
        /*
        int type;
        int ban;
        long  banTime;
        long refreshTime;
        long lastLoginTime;
        string  validateAddon;
        int validate;
        int version;
        */
    };
    sequence<UserDataResponse> UserDataResponseList;
    interface UserCenterServiceHandler{
        UserDataResponseList walkUser(long uuid, int size);
        UserDataResponse registerUser(string name,string password,string countryCode,string phone,string ip,string device) throws RegisterFailedException;
        UserDataResponse getUserByUuid(long uuid);
        UserDataResponse getUserByPhone(string countryCode,string phone);
        UserDataResponse loginByName(string name,string password, string device) throws LoginFailedException;
        UserDataResponse loginByPhone(string countryCode,string phone,string password, string device)  throws LoginFailedException;
        UserDataResponse loginByMessage(string countryCode,string phone, string device)  throws LoginFailedException;
        UserDataResponse logout(long uuid, string device);
        int updateUserSpaceUsage(long uuid, long spaceUsed);
        bool changePassword(long uuid,string oldPassword,string newPassword);
        bool changePasswordByUuid(long uuid,string newPassword);
        /*
        UserResponse getNextUser(long userId);
        int sendMessage(string countryCode,string phone,int flag,string validateCode,int expireInSeconds) throws RegisterFailedException;
        bool validateMessage(string countryCode,string phone,int flag,string validateCode,bool deleteIfSuccess) throws RegisterFailedException;
        UserResponse getUserByUuid(long uuid);
        UserResponse getUserByPhone(string countryCode,string phone);
        bool checkUserExistsByName(string name);
        bool checkUserExistsByEmail(string email);
        bool checkUserExistsByPhone(string countryCode,string phone);
        bool changePassword(long uuid,string oldPassword,string newPassword);
        bool changePasswordByMessage(long userId,string countryCode,string phone,string newPassword);
        UserResponse loginByPhone(string countryCode,string phone,bool isMobile)throws LoginFailedException;
        bool logout(long userId,bool isMobile)throws LoginFailedException;
        UserResponse checkUserValidByName(string name,string password, bool isMobile) throws LoginFailedException;
        UserResponse checkUserValidByEmail(string email,string password,bool isMobile) throws LoginFailedException;
        UserResponse checkUserValidByPhone(string countryCode,string phone,string password,bool isMobile) throws LoginFailedException;
        */
    };
};