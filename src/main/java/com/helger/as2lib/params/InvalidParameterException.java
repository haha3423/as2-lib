/**
 * The FreeBSD Copyright
 * Copyright 1994-2008 The FreeBSD Project. All rights reserved.
 * Copyright (C) 2013 Philip Helger ph[at]phloc[dot]com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE FREEBSD PROJECT ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE FREEBSD PROJECT OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the FreeBSD Project.
 */
package com.helger.as2lib.params;

import com.helger.as2lib.exception.OpenAS2Exception;

public class InvalidParameterException extends OpenAS2Exception
{
  private Object m_aTarget;
  private String m_sKey;
  private String m_sValue;

  public InvalidParameterException (final String msg, final Object target, final String key, final String value)
  {
    super (msg + " - " + toString (key, value));
    m_aTarget = target;
    m_sKey = key;
    m_sValue = value;
  }

  public InvalidParameterException (final Object target, final String key, final String value)
  {
    super (toString (key, value));
    m_aTarget = target;
    m_sKey = key;
    m_sValue = value;
  }

  public InvalidParameterException (final String msg)
  {
    super (msg);
  }

  public void setKey (final String string)
  {
    m_sKey = string;
  }

  public String getKey ()
  {
    return m_sKey;
  }

  public void setTarget (final Object object)
  {
    m_aTarget = object;
  }

  public Object getTarget ()
  {
    return m_aTarget;
  }

  public String getValue ()
  {
    return m_sValue;
  }

  public void setValue (final String value)
  {
    m_sValue = value;
  }

  public static void checkValue (final Object target, final String valueName, final Object value) throws InvalidParameterException
  {
    if (value == null)
    {
      throw new InvalidParameterException (target, valueName, null);
    }
  }

  public static String toString (final String key, final String value)
  {
    return "Invalid parameter value for " + key + ": " + value;
  }

}