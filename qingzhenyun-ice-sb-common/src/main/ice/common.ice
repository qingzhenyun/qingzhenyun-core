[["java:package:com.qingzhenyun.common.ice"]]
module common{
    class CommonPage {
        int page;
        int pageSize;
        int totalCount;
        int totalPage;
    };
    exception CommonRpcException{
        int innerCode;
        string innerMessage;
    };
    sequence<string> StringList;
}
