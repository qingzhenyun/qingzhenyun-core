#include <common.ice>
[["java:package:com.qingzhenyun.common.ice"]]
module userfile{
    exception FileOperationException extends common::CommonRpcException {
        int fileType;
    };

    class UserFileResponse{
        
        string  uuid;
        string  storeId;
        long    userId;
        // string  pathId;
        // int lft;
        // int rgt;
        string  path;
        string  name;
        string  ext;
        long    size;
        string  parent;
        int type;
        long    atime;
        long    ctime;
        long    mtime;
        int version;
        bool locking;
        int opt;
    };

    class SimpleFile {
        string  uuid;
        string  path;
    }

    class SimpleFileWithStoreId {
        string  uuid;
        string  path;
        string  storeId;
        int type;
    }

    sequence<UserFileResponse> UserFileResponseList;
    sequence<SimpleFile> SimpleFileList;
    class UserFilePageResponse extends common::CommonPage {
        UserFileResponseList list;
        //UserFileResponseList path;
        //UserFileResponse info;
    };
    /*
    class UserOfflineResponse{
        long    userId;
        string  taskHash;
        string  path;
        long size;
        string mime;
        string  name;
        string  files;
        string  copied;
        long    createTime;
        string  uuid;
        string  destUuid;
        int progress;
        int status;
    };
    */
    class FileOperation {
        long    taskId;
        long    userId;
        string  source;
        string  dest;
        int operation;
    };
    sequence<FileOperation> FileOperationList;
    sequence<SimpleFileWithStoreId> SimpleFileWithStoreIdList;
    /*
    sequence<UserOfflineResponse> UserOfflineResponseList;
    class UserOfflinePageResponse extends common::CommonPage{
        UserOfflineResponseList list;
    };
    */
    interface UserFileServiceHandler {
        FileOperationList fetchFileOperation(int size);
        UserFileResponse createDirectory(long userId, string parent, string path, string name);
        UserFileResponse createFile(long userId, string parent, string path, string name, long size, string storeId);
        UserFilePageResponse listDirectoryPage(long userId,string uuid,int type,int page,int pageSize,int orderBy);
        UserFileResponseList listDirectory(long userId,string uuid,int type,int start, int size,int orderBy);
        UserFileResponse get(long userId,string uuid,string path);
        SimpleFileWithStoreIdList getSimpleFileWithStoreIdList(long userId, common::StringList pathList);
        int rename(long userId,string uuid,string path, string newName);
        int move(long userId,string uuid,string path,string destUuid,string destPath);
        int copy(long userId,string uuid,string path,string destUuid,string destPath);
        int remove(long userId,string uuid,string path);
        int unlock(long userId,string uuid);
        int deleteFile(long userId,string uuid);
        int updateDirectorySize(long userId, string uuid, long fileSize);
        /*
        int removeOfflineTask(long userId,common::StringList taskHash) throws FileOperationException;
        void finishOfflineFileCopy(string taskHash,long userId);
        void finishAllOfflineFileCopy(string taskHash);
        UserOfflinePageResponse listOfflinePage(long userId,int page,int pageSize,int order) throws FileOperationException;
        UserOfflineResponse createOfflineTask(long userId,string taskHash,string path,string name,string files,string uuid) throws FileOperationException;
        UserOfflineResponseList fetchUserOfflineTask(string taskHash);
        UserOfflineResponse fetchUserOfflineTaskById(string taskHash,long userId) throws FileOperationException;
        bool reportFileCopied(long userId,string taskHash,string copied,string destUuid,string mime,long size) throws FileOperationException;
        */
        /* FileSystem */
        /*
        UserFileResponse copyStoreFileToPath(string storeId, string mime, long size,int preview,long userId,string parent,string path,string sourcePath,string filename) throws FileOperationException;
        UserFileResponse createDirectory(long userId, string parent, string path, string name,bool autoRename) throws FileOperationException;
        UserFileResponse get(long userId,string uuid,string path) throws FileOperationException;
        UserFilePageResponse listDirectoryPage(long userId,string parent,string path,int fileType, int recycle,int page,int pageSize,int orderBy) throws FileOperationException;
        UserFileResponseList listDirectory(long userId,string parent,string path,int fileType, int recycle,int start,int size,int orderBy) throws FileOperationException;
        UserFileResponseList listDirectoryByPreview(long userId,string parent,string path,int fileType, int recycle,int preview,int start,int size,int orderBy) throws FileOperationException;
        UserFileResponseList listDirectoryByMime(long userId,string parent,string path,int fileType, int recycle,string mime,int start,int size,int orderBy) throws FileOperationException;
        UserFileResponse copyStoreFileToUserFile(string storeId, string mime, long size,int preview,long userId,string parent,string path,string name, bool overwrite) throws FileOperationException;
        bool checkUserFileExists(long userId,string parent, string path,string name);

        bool move(long userId,SimpleFileList source,string parent, string path, bool overwrite) throws FileOperationException;
        bool rename(long userId,string uuid,string path,string newName,bool overwrite) throws FileOperationException;
        bool recycle(long userId,SimpleFileList source) throws FileOperationException;
        bool remove(long userId,SimpleFileList source) throws FileOperationException;
        UserAsyncTask fetchAsyncTask();
        void finishAsyncTask(UserAsyncTask task);
        void fixParent(long userId, string uuid, int recycle);
        void deleteByUuid(long userId, string uuid);
        */

        // bool recycle(long userId,SimpleFileList source) throws FileOperationException;
        // bool forceDelete(long userId,SimpleFileList source) throws FileOperationException;
        //Checked.
        //UserFileResponseList getFilePath(string uuid,long userId) throws FileOperationException;
        //UserFileResponse get(string uuid,long userId,string name) throws FileOperationException;
        //UserFileResponse getWithoutPath(string uuid,long userId,string name) throws FileOperationException;
        //UserFileResponse rename(string uuid,long userId,string name) throws FileOperationException;
        //bool couldCreateFile(string parent,long userId,string name,int fileType)throws FileOperationException;
        //UserFileResponse createUserFile(string parent,long userId,string name,string storeId,long size,string mime,int preview,int fileType,bool override) throws FileOperationException;
        //int batchMove(common::StringList uuid,string parent,long userId) throws FileOperationException;
        //
        //int batchRecycle(common::StringList uuid,long userId,bool recycle) throws FileOperationException;

    };
};