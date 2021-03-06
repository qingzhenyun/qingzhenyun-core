// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.7.0
//
// <auto-generated>
//
// Generated from file `userfile.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package com.qingzhenyun.common.ice.userfile;

public class UserOfflinePageResponse extends com.qingzhenyun.common.ice.common.CommonPage {
    public static final long serialVersionUID = -7132854797303454448L;
    public UserOfflineResponse[] list;

    public UserOfflinePageResponse() {
        super();
    }

    public UserOfflinePageResponse(int page, int pageSize, int totalCount, int totalPage, UserOfflineResponse[] list) {
        super(page, pageSize, totalCount, totalPage);
        this.list = list;
    }

    public static String ice_staticId() {
        return "::userfile::UserOfflinePageResponse";
    }

    public UserOfflinePageResponse clone() {
        return (UserOfflinePageResponse) super.clone();
    }

    @Override
    public String ice_id() {
        return ice_staticId();
    }

    @Override
    protected void _iceWriteImpl(com.zeroc.Ice.OutputStream ostr_) {
        ostr_.startSlice(ice_staticId(), -1, false);
        UserOfflineResponseListHelper.write(ostr_, list);
        ostr_.endSlice();
        super._iceWriteImpl(ostr_);
    }

    @Override
    protected void _iceReadImpl(com.zeroc.Ice.InputStream istr_) {
        istr_.startSlice();
        list = UserOfflineResponseListHelper.read(istr_);
        istr_.endSlice();
        super._iceReadImpl(istr_);
    }
}
