[["java:package:com.qingzhenyun.common.ice"]]
module cloudstore {
    class CloudStoreResponse {
        string  hash;
        long    size;
        string  mime;
        long    uploadUser;
        long    ctime;
        string  originalFilename;
        string  bucket;
        string  key;
        int type;
        int preview;
        string  uploadIp;
        int flag;
        bool hasPreview;
    };
    class KnownMimeResponse {
        long    uuid;
        string  mime;
        int  previewState;
        int level;
        int isReg;
    }

    class UnknownMimeResponse {
        long    uuid;
        string  mime;
        long    createTime;
    }

    struct CloudStoreTokenResponse {
        string  name;
        string  parent;
        string  path;
        string  token;
        int type;
        string  uploadUrl;
        int version;
    };

    class SimpleDetailResponse {
        string  hash;
        long    size;
        int preview;
        string  mime;
        int flag;
        bool hasPreview;
    };

    class PreviewTaskResponse {
        string  hash;
        int status;
        string  info;
        long    createTime;
        long    updateTime;
     };

    class CloudStoreResponseEx extends CloudStoreResponse {
        string  downloadAddress;
    };
    sequence<int> IntList;

    sequence<UnknownMimeResponse> UnknownMimeResponseList;
    sequence<KnownMimeResponse> KnownMimeResponseList;
    sequence<string> StringSequence;
    sequence<PreviewTaskResponse> PreviewTaskResponseList;
    sequence<SimpleDetailResponse> SimpleDetailResponseSequence;


    interface CloudStoreServiceHandler{
        KnownMimeResponseList getKnownMimeList();
        UnknownMimeResponseList getUnknownMimeList();
        bool addUnknownMime(string mime);
        CloudStoreTokenResponse createUploadToken(long userId,string parent,string path,string name, string originalFilename);
        CloudStoreResponse get(string hash);
        CloudStoreResponseEx getEx(string hash,long userId,bool internal);
        CloudStoreResponse uploadFile(string response);
        SimpleDetailResponseSequence getList(StringSequence hashList);
        SimpleDetailResponse getSimple(string hash);
        PreviewTaskResponseList fetchPreviewTask(string serverId,IntList status,int nextStatus, int size);
        PreviewTaskResponse updatePreviewTask(string serverId,string hash, PreviewTaskResponse data);
        PreviewTaskResponse updatePreviewTaskInfo(string serverId,string hash, int status, string info);
        bool finishPreviewTask(string serverId, string hash, int status);
        bool updateMimeAndKey(string serverId, string hash, string mime, bool finish, int status ,string bucket, string key);
    }
}