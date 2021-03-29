package com.viatom.demo.spotcheckm.ble

import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import java.util.ArrayList
import kotlin.concurrent.thread


class BleMsgUtils {
    companion object {

        private const val TAG = "OxySmart_BleService"

        // mClients
        @JvmStatic
        var mClients: ArrayList<Messenger> = ArrayList<Messenger>()

        /**
         * add client
         */
        fun addClient(replyTo: Messenger) {
            mClients.add(replyTo)
        }

        fun removeClient(replyTo: Messenger) {
            mClients.remove(replyTo)
        }

        /**
         * broadcast
         *
         * send msg to all clients
         */
        fun broadcastMsg(mSrc: Messenger, what: Int, obj: Any) {
            thread(start = true) {
                for (client in mClients) {
                    sendMsg(mSrc, client, what, obj)
                }
            }
        }

        fun broadcastMsg(mSrc: Messenger, what: Int) {
            thread(start = true) {
                for (client in mClients) {
                    sendMsg(mSrc, client, what)
                }
            }
        }

        fun broadcastMsg(mSrc: Messenger, what: Int, code: Int, obj: Any) {
            thread(start = true) {
                for (client in mClients) {
                    sendMsg(mSrc, client, what, code, obj)
                }
            }
        }

        fun broadcastMsg(mSrc: Messenger, what: Int, code: Int) {
            thread(start = true) {
                for (client in mClients) {
                    sendMsg(mSrc, client, what, code)
                }
            }
        }


        /**
         * msg from mSrc to mTar
         * send msg command
         */
        fun sendMsg (mSrc: Messenger, mTar: Messenger, what: Int) {
            val msg: Message =  Message.obtain(null, what)
            msg.replyTo = mSrc
            try {
                mTar.send(msg)
            } catch (e: RemoteException) {
                Log.d(TAG, e.toString())
                e.printStackTrace()
            }
        }

        /**
         * msg from mSrc to mTar
         * send msg: cmd, code
         * most use in service response
         */
        fun sendMsg(mSrc: Messenger, mTar: Messenger, what: Int, code: Int) {
            val msg: Message =  Message.obtain(null, what, code, 0)
            msg.replyTo = mSrc
            try {
                mTar.send(msg)
            } catch (e: RemoteException) {
                Log.d(TAG, e.toString())
                e.printStackTrace()
            }
        }

        /**
         * msg from mSrc to mTar
         * send msg: cmd, code
         * most use in service response
         */
        fun sendMsg(mSrc: Messenger, mTar: Messenger, what: Int, arg1: Int, arg2: Int) {
            val msg: Message =  Message.obtain(null, what, arg1, arg2)
            msg.replyTo = mSrc
            try {
                mTar.send(msg)
            } catch (e: RemoteException) {
                Log.d(TAG, e.toString())
                e.printStackTrace()
            }
        }

        /**
         * msg from mSrc to mTar
         * send msg command with object
         */
        fun sendMsg (mSrc: Messenger, mTar: Messenger, what: Int, obj: Any) {

            val msg: Message =  Message.obtain(null, what, obj)
            msg.replyTo = mSrc
            try {
                mTar.send(msg)
            } catch (e: RemoteException) {
                Log.d(TAG, e.toString())
                e.printStackTrace()
            }
        }

        /**
         * msg from mSrc to mTar
         * send msg: cmd, code and object
         * most use in service response
         */
        fun sendMsg(mSrc: Messenger, mTar: Messenger, what: Int, arg1: Int, obj: Any) {
            val msg: Message =  Message.obtain(null, what, arg1, 0, obj)
            msg.replyTo = mSrc
            try {
                mTar.send(msg)
            } catch (e: RemoteException) {
                Log.d(TAG, e.toString())
                e.printStackTrace()
            }
        }



        /**
         * register client
         */
        fun register(mSrc: Messenger, mTar: Messenger) {
            val msg: Message =  Message.obtain(null, BleMsg.MSG_REGISTER)
            msg.replyTo = mSrc
            try {
                mTar.send(msg)
            } catch (e: RemoteException) {
                Log.d(TAG, e.toString())
                e.printStackTrace()
            }
        }

        /**
         * unregister client
         */
        fun unregister(mSrc: Messenger, mTar: Messenger) {
            val msg: Message =  Message.obtain(null, BleMsg.MSG_UNREGISTER)
            msg.replyTo = mSrc
            try {
                mTar.send(msg)
            } catch (e: RemoteException) {
                Log.d(TAG, e.toString())
                e.printStackTrace()
            }
        }
    }
}