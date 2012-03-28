/*
 *  Aprof - Java Memory Allocation Profiler
 *  Copyright (C) 2002-2012  Devexperts LLC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.aprof;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Roman Elizarov
 */
public class AllocSpeedTest {
	public static void main(String[] args) {
		new AllocSpeedTest().go();
	}

	private final NumberFormat nf = NumberFormat.getInstance(Locale.US);

	private long total_bobjs;
	private long total_bytes;

	private long total_cobjs;
	private long total_chars;

	private void go() {
		for (int pass = 1; pass <= 3; pass++) {
			System.out.println("Pass #" + pass);
			System.out.println("Bytes via new byte[]...");
			runByteTest();
			System.out.println("Chars via StringBuilder.toString()...");
			runCharTest();
		}
		System.out.println("TOTAL of " + nf.format(total_bytes) + " bytes in " + nf.format(total_bobjs) + " objects");
		System.out.println("TOTAL of " + nf.format(total_chars) + " chars in " + nf.format(total_cobjs) + " objects");
	}

	private void runByteTest() {
		System.gc();
		for (int size = 1; size <= 1 << 18; size *= 2)
			runByteTest(size);
	}

	private void runCharTest() {
		System.gc();
		for (int size = 1; size <= 1 << 17; size *= 2)
			runCharTest(size);
	}

	private int getBytes(int size) {
		int bytes;
		if (size <= 2)
			bytes = 1 << 25;
		else if (size <= 4)
			bytes = 1 << 26;
		else if (size <= 8)
			bytes = 1 << 27;
		else if (size <= 16)
			bytes = 1 << 28;
		else if (size <= 64)
			bytes = 1 << 29;
		else if (size <= 32768)
			bytes = 1 << 30;
		else if (size <= 65536)
			bytes = 1 << 28;
		else if (size <= 131072)
			bytes = 1 << 27;
		else
			bytes = 1 << 26;
		return bytes;
	}

	private byte[] runByteTest(int size) {
		int bytes = getBytes(size);
		int count = bytes / size;
		bytes = count * size;
		long time = System.currentTimeMillis();
		byte[] b = null;
		for (int i = 0; i < count; i++)
			b = new byte[size];
		time = System.currentTimeMillis() - time;
		printByteResult(size, count, bytes, time);
		total_bobjs += count;
		total_bytes += bytes;
		return b;
	}

	private void printByteResult(int size, int count, int bytes, long time) {
		System.out.println("Size = " + size + ": " +
			"Allocated " + nf.format(count) + " objs with " + nf.format(bytes) + " bytes in " + time + " ms " +
			"(" + nf.format(1000L * bytes / time) + " bps, " +
			nf.format(1000L * count / time) + " ops)");
	}

	private String runCharTest(int size) {
		StringBuilder sb = new StringBuilder(size);
		sb.setLength(size);
		int chars = getBytes(size) / 3;
		int count = chars / size;
		chars = count * size;
		long time = System.currentTimeMillis();
		String s = null;
		for (int i = 0; i < count; i++)
			s = sb.toString();
		time = System.currentTimeMillis() - time;
		printCharResult(size, count, chars, time);
		total_cobjs += count;
		total_chars += chars;
		return s;
	}

	private void printCharResult(int size, int count, int chars, long time) {
		System.out.println("Size = " + size + ": " +
			"Allocated " + nf.format(count) + " objs with " + nf.format(chars) + " chars in " + time + " ms " +
			"(" + nf.format(1000L * chars / time) + " cps, " +
			nf.format(1000L * count / time) + " ops)");
	}
}