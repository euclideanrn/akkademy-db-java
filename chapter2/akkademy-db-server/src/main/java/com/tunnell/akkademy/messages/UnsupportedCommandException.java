package com.tunnell.akkademy.messages;

import java.io.Serializable;

/**
 * Created by TunnellZhao on 2017/5/5.
 *
 * Throws {@link UnsupportedCommandException} if the request command cannot be recognized
 */
public class UnsupportedCommandException extends Exception implements Serializable {
}
