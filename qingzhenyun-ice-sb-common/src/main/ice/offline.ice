#include <common.ice>
[["java:package:com.qingzhenyun.common.ice"]]
module offline{

    class SystemOfflineTaskResponse{
        string  taskId;
        int type;
        string  name;
        int status;
        string  serverId;
        long    createTime;
        long    updateTime;
        long    createUser;
        string  createIp;
        string  detail;
        long    size;
        int progress;
        long    finishedSize;
        int errorCode;
        string mime;
    };
    class SystemTaskDetailResponse{
        string  taskId;
        string  path;
        long    size;
        long    completed;
        int progress;
        int order;
        string storeId;
    }
    sequence<SystemTaskDetailResponse> SystemTaskDetailResponseList;
    class SystemOfflineTaskWithDetailResponse{
        SystemOfflineTaskResponse task;
        SystemTaskDetailResponseList detail;
    };
    sequence<SystemOfflineTaskResponse> SystemOfflineTaskResponseList;
    sequence<int> IntList;
    class UserOfflineTaskResponse{
        long    userId;
        string  taskId;
        string  copyFile;
        string  copiedFile;
        long    createTime;
        string  savePath;
        string  filePath;
        long    copied;
        int    status;
     };
    class CopyTaskResponse {
        string  taskId;
        long    userId;
        int progress;
        long    updateTime;
        int status;
        long    copied;
        long    needCopySize;
    };
    sequence<UserOfflineTaskResponse> UserOfflineTaskResponseList;
    sequence<CopyTaskResponse> CopyTaskResponseList;
    class UserOfflinePageResponse extends common::CommonPage{
            UserOfflineTaskResponseList list;
     };
    sequence<string> StringSequence;
    interface OfflineTaskServiceHandler{
        SystemOfflineTaskResponse addSystemTask(string taskId,int type,string  name, long createUser,string createIp, string detail);
        SystemOfflineTaskResponseList getSystemOfflineTaskList(StringSequence taskIdList);
        SystemOfflineTaskResponseList fetchTask(string serverId,IntList types,IntList status,int nextStatus, int size);
        bool updateDownloadingStatus(string taskId, string serverId,int status, string message, bool force);
        SystemOfflineTaskWithDetailResponse getSystemTask(string taskId);
        bool updateSystemTaskMetadata(SystemOfflineTaskWithDetailResponse data);
        bool updateSystemTaskDetail(SystemTaskDetailResponse data);
        bool finishOfflineTask(string taskId, int errorCode);
        bool updateTaskMime(string taskId, string mime);
        bool updateTaskProgress(string taskId, int status ,int progress, long size, long finishedSize);
        UserOfflineTaskResponse addUserTask(string taskId, long userId, string copyFile, string savePath);
        CopyTaskResponseList fetchCopyTask(int start, int size, int status, string taskId);
        UserOfflineTaskResponse fetchUserTask(string taskId, long userId);
        bool deleteCopyTask(string taskId);
        bool copyUserFile(string taskId, long userId, int status, int progress, long copied,long needCopySize, string copiedFile,string filePath);
        bool finishCopy(string taskId, long userId);
        UserOfflineTaskResponseList listOfflineTask(long userId,int start,int size,int order);
        UserOfflinePageResponse listOfflineTaskPage(long userId,int page,int pageSize,int order);

        //start:Int, size:Int, status: Int?, taskId: Skring
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