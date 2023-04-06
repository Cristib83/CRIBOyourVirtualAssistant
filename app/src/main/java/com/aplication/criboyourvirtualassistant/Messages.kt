package com.aplication.criboyourvirtualassistant

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.commons.models.MessageContentType
import java.util.*

class Messages(val mid: String, val mtext: String, var muser: IUser, var mdate: Date, var  mUrl: String): IMessage , MessageContentType.Image{
    override fun getId(): String {
        return mid;
    }

    override fun getText(): String {
       return mtext;
    }

    override fun getUser(): IUser {
        return muser;
    }

    override fun getCreatedAt(): Date {
       return mdate;
    }

    override fun getImageUrl(): String? {

       if (mUrl.equals("")) {
           return null
       }

      return mUrl;
    }



}