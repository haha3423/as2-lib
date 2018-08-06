/**
 * The FreeBSD Copyright
 * Copyright 1994-2008 The FreeBSD Project. All rights reserved.
 * Copyright (C) 2013-2018 Philip Helger philip[at]helger[dot]com
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
package com.helger.as2lib.util.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import javax.mail.util.SharedFileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.mutable.MutableLong;

/**
 * Stores the content of the input {@link InputStream} in a temporary file, and
 * opens {@link SharedFileInputStream} on that file. When the stream is closed,
 * the file will be deleted, and the input stream will be closed.
 */
public class TempSharedFileInputStream extends SharedFileInputStream
{
  private static final Logger LOGGER = LoggerFactory.getLogger (TempSharedFileInputStream.class);

  private final File m_aTempFile;
  private final InputStream m_aSrcIS;

  private TempSharedFileInputStream (@Nonnull final File aFile,
                                     @Nonnull @WillNotClose final InputStream aSrcIS) throws IOException
  {
    super (aFile);
    m_aSrcIS = aSrcIS;
    m_aTempFile = aFile;
  }

  /**
   * close - Do nothing, to prevent early close, as the cryptographic processing
   * stages closes their input stream
   */
  @Override
  public void close () throws IOException
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("close() called, doing nothing.");
  }

  /**
   * finalize - closes also the input stream, and deletes the backing file
   */
  @Override
  // TODO get rid of this
  public void finalize () throws IOException
  {
    try
    {
      super.finalize ();
      closeAll ();
    }
    catch (final Throwable t)
    {
      LOGGER.error ("Exception in finalize()", t);
      throw new IOException (t.getClass ().getName () + ":" + t.getMessage ());
    }
  }

  /**
   * closeAll - closes the input stream, and deletes the backing file
   *
   * @throws IOException
   *         in case of error
   */
  public void closeAll () throws IOException
  {
    m_aSrcIS.close ();
    super.close ();
    if (m_aTempFile.exists ())
    {
      if (!m_aTempFile.delete ())
      {
        LOGGER.error ("Failed to delete file {}", m_aTempFile.getAbsolutePath ());
      }
    }
  }

  /**
   * Stores the content of the input {@link InputStream} in a temporary file (in
   * the system temporary directory.
   *
   * @param aIS
   *        {@link InputStream} to read from
   * @param sName
   *        name to use in the temporary file to link it to the delivered
   *        message. May be null
   * @return The created {@link File}
   * @throws IOException
   *         in case of IO error
   */
  @Nonnull
  protected static File storeContentToTempFile (@Nonnull final InputStream aIS,
                                                @Nonnull final String sName) throws IOException
  {
    // create temp file and write steam content to it
    // name may contain ":" on Windows and that would fail the tests!
    final String sSuffix = FilenameHelper.getAsSecureValidASCIIFilename (sName != null ? sName : "tmp");
    final File aDestFile = File.createTempFile ("AS2TempSharedFileIS", sSuffix);

    try (final FileOutputStream aOS = new FileOutputStream (aDestFile))
    {
      final MutableLong aCount = new MutableLong (0);
      StreamHelper.copyInputStreamToOutputStream (aIS, aOS, aCount);
      if (LOGGER.isInfoEnabled ())
        LOGGER.info (aCount.longValue () + " bytes copied to " + aDestFile.getAbsolutePath ());
    }
    return aDestFile;
  }

  /**
   * Stores the content of the input {@link InputStream} in a temporary file (in
   * the system temporary directory, and opens {@link SharedFileInputStream} on
   * that file.
   *
   * @param aIS
   *        {@link InputStream} to read from
   * @param sName
   *        name to use in the temporary file to link it to the delivered
   *        message. May be null
   * @return {@link TempSharedFileInputStream} on the created temporary file.
   * @throws IOException
   *         in case of IO error
   */
  @Nonnull
  public static TempSharedFileInputStream getTempSharedFileInputStream (@Nonnull @WillNotClose final InputStream aIS,
                                                                        @Nonnull final String sName) throws IOException
  {
    final File aDest = storeContentToTempFile (aIS, sName);
    return new TempSharedFileInputStream (aDest, aIS);
  }
}