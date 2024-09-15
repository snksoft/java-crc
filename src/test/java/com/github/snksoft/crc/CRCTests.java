package com.github.snksoft.crc;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class CRCTests {
    private static final String longText = "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits.";

    private static byte[] testArray = null;

    static {
        testArray = new byte[256];
        for (int i=0; i<testArray.length; i++)
        {
            testArray[i] = (byte)(i&0x0FF);
        }
    }

    interface TestFunction3<A, B, C> {
        public void apply(A a, B b, C c);
    }
    @Test
    public void testPolynomialsOfVariousWidth() {
        TestFunction3<CRC.Parameters, Long, byte[]> byteTest = (crcParams, crc , dataBytes) -> {
            ByteBuffer byteBuffer = ByteBuffer.wrap(dataBytes);

            long calculated1 = CRC.calculateCRC(crcParams, dataBytes);
            long calculated1b = CRC.calculateCRC(crcParams, byteBuffer);

            assertTrue(calculated1 == crc);
            assertTrue(calculated1b == crc);

            CRC table = new CRC(crcParams);
            long tableBasedCrc = table.calculateCRC(dataBytes);
            assertTrue(tableBasedCrc == crc);
            // same, using ByteBuffer
            long tableBasedCrcByteBuffer = table.calculateCRC(byteBuffer);
            assertTrue(tableBasedCrcByteBuffer == crc);

            long tableBased2 = table.init();
            tableBased2 = table.update(tableBased2, dataBytes);
            tableBased2 = table.finalCRC(tableBased2);
            assertTrue(tableBased2 == crc);

            long tableBased2b = table.init();
            tableBased2b = table.update(tableBased2b, byteBuffer);
            tableBased2b = table.finalCRC(tableBased2b);
            assertTrue(tableBased2b == crc);

        };
        TestFunction3<CRC.Parameters, Long, String> doTest = (crcParams, crc , data) -> {
            byte[] dataBytes = data.getBytes();
            byteTest.apply(crcParams, crc, data.getBytes());
        };

        //where:
        //crcParams                                                | crc  | data
        doTest.apply(new CRC.Parameters(3, 0x03, 0x00, false, false, 0x7), 0x04L, "123456789"); // CRC-3/GSM
        doTest.apply(new CRC.Parameters(3, 0x03, 0x00, false, false, 0x7), 0x06L, longText);
        byteTest.apply(new CRC.Parameters(3, 0x03, 0x00, false, false, 0x7), 0x02L, testArray);
        doTest.apply(new CRC.Parameters(3, 0x03, 0x07, true,  true,  0x0), 0x06L, "123456789"); // CRC-3/ROHC
        doTest.apply(new CRC.Parameters(3, 0x03, 0x07, true,  true,  0x0), 0x03L, longText);
        doTest.apply(new CRC.Parameters(4, 0x03, 0x00, true,  true,  0x0), 0x07L, "123456789"); // CRC-4/ITU
        doTest.apply(new CRC.Parameters(4, 0x03, 0x0f, false, false, 0xf), 0x0bL, "123456789"); // CRC-4/INTERLAKEN
        doTest.apply(new CRC.Parameters(4, 0x03, 0x0f, false, false, 0xf), 0x01L, longText); // CRC-4/INTERLAKEN
        byteTest.apply(new CRC.Parameters(4, 0x03, 0x0f, false, false, 0xf), 0x07L, testArray); // CRC-4/INTERLAKEN
        doTest.apply(new CRC.Parameters(5, 0x09, 0x09, false, false, 0x0), 0x00L, "123456789"); // CRC-5/EPC
        doTest.apply(new CRC.Parameters(5, 0x15, 0x00, true,  true,  0x0), 0x07L, "123456789"); // CRC-5/ITU
        doTest.apply(new CRC.Parameters(6, 0x27, 0x3f, false, false, 0x0), 0x0dL, "123456789"); // CRC-6/CDMA2000-A
        doTest.apply(new CRC.Parameters(6, 0x07, 0x3f, false, false, 0x0), 0x3bL, "123456789"); // CRC-6/CDMA2000-B
        byteTest.apply(new CRC.Parameters(6, 0x07, 0x3f, false, false, 0x0), 0x24L, testArray); // CRC-6/CDMA2000-B
        doTest.apply(new CRC.Parameters(7, 0x09, 0x00, false, false, 0x0), 0x75L, "123456789"); // CRC-7
        byteTest.apply(new CRC.Parameters(7, 0x09, 0x00, false, false, 0x0), 0x78L, testArray); // CRC-7
        doTest.apply(new CRC.Parameters(7, 0x4f, 0x7f, true,  true,  0x0), 0x53L, "123456789"); // CRC-7/ROHC

        doTest.apply(new CRC.Parameters(12, 0xd31, 0x00, false, false, 0xfff), 0x0b34L, "123456789"); // CRC-12/GSM
        doTest.apply(new CRC.Parameters(12, 0x80f, 0x00, false, true, 0x00), 0x0dafL, "123456789"); // CRC-12/UMTS
        doTest.apply(new CRC.Parameters(13, 0x1cf5, 0x00, false, false, 0x00), 0x04faL, "123456789"); // CRC-13/BBC
        doTest.apply(new CRC.Parameters(14, 0x0805, 0x00, true, true, 0x00), 0x082dL, "123456789"); // CRC-14/DARC
        doTest.apply(new CRC.Parameters(14, 0x202d, 0x00, false, false, 0x3fff), 0x30aeL, "123456789"); // CRC-14/GSM

        doTest.apply(new CRC.Parameters(15, 0x4599, 0x00, false, false, 0x00), 0x059eL, "123456789"); // CRC-15
        doTest.apply(new CRC.Parameters(15, 0x4599, 0x00, false, false, 0x00), 0x2857L, longText);
        doTest.apply(new CRC.Parameters(15, 0x6815, 0x00, false, false, 0x0001), 0x2566L, "123456789"); // CRC-15/MPT1327

        doTest.apply(new CRC.Parameters(21, 0x102899, 0x000000, false, false, 0x000000), 0x0ed841L, "123456789"); // CRC-21/CAN-FD
        doTest.apply(new CRC.Parameters(24, 0x864cfb, 0xb704ce, false, false, 0x000000), 0x21cf02L, "123456789"); // CRC-24
        doTest.apply(new CRC.Parameters(24, 0x5d6dcb, 0xfedcba, false, false, 0x000000), 0x7979bdL, "123456789"); // CRC-24/FLEXRAY-A
        doTest.apply(new CRC.Parameters(24, 0x00065b, 0x555555,  true,  true, 0x000000), 0xc25a56L, "123456789"); // "CRC-24/BLE"

        doTest.apply(new CRC.Parameters(31, 0x04c11db7, 0x7fffffff, false, false, 0x7fffffff), 0x0ce9e46cL, "123456789"); // CRC-31/PHILIPS
    }

    @Test
    public void testCRC8() {
        TestFunction3<CRC.Parameters, Long, String> doTest = (crcParams, crc , data) -> {
            byte[] dataBytes = data.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.wrap(dataBytes);
            long calculated1 = CRC.calculateCRC(crcParams, dataBytes);
            long calculated1b = CRC.calculateCRC(crcParams, byteBuffer);

            // same test using table driven
            CRC tableDriven = new CRC(crcParams);
            long calculated2 = tableDriven.calculateCRC(dataBytes);
            long calculated2b = tableDriven.calculateCRC(byteBuffer);

            // same test feeding data in chunks of different size
            long curValue = tableDriven.init();
            int start = 0;
            int step = 1;
            while (start < dataBytes.length) {
                int end = start + step;
                if (end > dataBytes.length) {
                    end = dataBytes.length;
                }
                curValue = tableDriven.update(curValue, dataBytes, start, end - start);
                start = end;
                step *= 2;
            }
            byte calculated3 = tableDriven.finalCRC8(curValue);

            // same test feeding data in chunks of different size, but using ByteBuffer
            curValue = tableDriven.init();
            start = 0;
            step = 3;
            int length = dataBytes.length; // ByteBuffer limit() is same as dataBytes.length()
            while (start < length) {
                int end = start + step;
                if (end > length) {
                    end = length;
                }
                curValue = tableDriven.update(curValue, byteBuffer, start, end - start);
                start = end;
            }
            byte calculated3b = tableDriven.finalCRC8(curValue);

            assertTrue(calculated1 == crc);
            assertTrue(calculated2 == crc);
            assertTrue(calculated3 == (byte) (crc & 0x00FF));
            assertTrue(calculated1b == crc);
            assertTrue(calculated2b == crc);
            assertTrue(calculated3b == (byte) (crc & 0x00FF));
        };

        doTest.apply(new CRC.Parameters(8, 0x07, 0, false, false, 0), 0xf4L, "123456789");
    }

    @Test
    public void testCRC16() {
        TestFunction3<CRC.Parameters, Long, String> doTest = (crcParams, crc , data) -> {
            byte[] dataBytes = data.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.wrap(dataBytes);
            long calculated1 = CRC.calculateCRC(crcParams, dataBytes);
            long calculated1b = CRC.calculateCRC(crcParams, byteBuffer);

            // same test using table driven
            CRC tableDriven = new CRC(crcParams);
            long calculated2 = tableDriven.calculateCRC(dataBytes);
            long calculated2b = tableDriven.calculateCRC(byteBuffer);

            // same test feeding data in chunks of different size
            long curValue = tableDriven.init();
            int start = 0;
            int step = 1;
            while (start < dataBytes.length) {
                int end = start + step;
                if (end > dataBytes.length) {
                    end = dataBytes.length;
                }
                curValue = tableDriven.update(curValue, dataBytes, start, end - start);
                start = end;
                step *= 2;
            }
            short calculated3 = tableDriven.finalCRC16(curValue);

            // same test feeding data in chunks of different size
            curValue = tableDriven.init();
            start = 0;
            step = 7;
            int length = byteBuffer.limit();
            while (start < length) {
                int end = start + step;
                if (end > length) {
                    end = length;
                }
                curValue = tableDriven.update(curValue, byteBuffer, start, end - start);
                start = end;
            }
            short calculated3b = tableDriven.finalCRC16(curValue);

            assertTrue(calculated1 == crc);
            assertTrue(calculated2 == crc);
            assertTrue(calculated3 == (short) (crc & 0x0000FFFF));
            assertTrue(calculated1b == crc);
            assertTrue(calculated2b == crc);
            assertTrue(calculated3b == (short) (crc & 0x0000FFFF));
        };

        doTest.apply(CRC.Parameters.CCITT, 0x29B1L, "123456789");
        doTest.apply(CRC.Parameters.CCITT, 0xDA31L, "12345678901234567890");
        doTest.apply(CRC.Parameters.CCITT, 0xC87EL, "Introduction on CRC calculations");
        doTest.apply(CRC.Parameters.CCITT, 0xD6EDL, "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits.");

        doTest.apply(CRC.Parameters.XMODEM, 0x31C3L, "123456789");
        doTest.apply(CRC.Parameters.XMODEM, 0x2C89L, "12345678901234567890");
        doTest.apply(CRC.Parameters.XMODEM, 0x3932L, "Introduction on CRC calculations");
        doTest.apply(CRC.Parameters.XMODEM, 0x4E86L, "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits.");

        doTest.apply(CRC.Parameters.XMODEM2, 0x0C73L, "123456789");
        doTest.apply(CRC.Parameters.XMODEM2, 0x122EL, "12345678901234567890");
        doTest.apply(CRC.Parameters.XMODEM2, 0x0638L, "Introduction on CRC calculations");
        doTest.apply(CRC.Parameters.XMODEM2, 0x187AL, "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits.");
    }


    @Test
    public void testCRC32() {
        TestFunction3<CRC.Parameters, Long, String> doTest = (crcParams, crc , data) -> {
            byte[] dataBytes = data.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.wrap(dataBytes);
            long calculated1 = CRC.calculateCRC(crcParams, dataBytes);
            long calculated1b = CRC.calculateCRC(crcParams, byteBuffer);

            // same test using table driven
            CRC tableDriven = new CRC(crcParams);
            long calculated2 = tableDriven.calculateCRC(dataBytes);
            long calculated2b = tableDriven.calculateCRC(byteBuffer);

            // same test feeding data in chunks of different size
            long curValue = tableDriven.init();
            int start = 0;
            int step = 1;
            while (start < dataBytes.length) {
                int end = start + step;
                if (end > dataBytes.length) {
                    end = dataBytes.length;
                }
                curValue = tableDriven.update(curValue, dataBytes, start, end - start);
                start = end;
                step *= 2;
            }
            int calculated3 = tableDriven.finalCRC32(curValue);

            // same test feeding data in chunks of different size, but using byte buffer
            curValue = tableDriven.init();
            start = 0;
            step = 1;
            while (start < dataBytes.length) {
                int end = start + step;
                if (end > dataBytes.length) {
                    end = dataBytes.length;
                }
                curValue = tableDriven.update(curValue, byteBuffer, start, end - start);
                start = end;
                step *= 2;
            }
            int calculated3b = tableDriven.finalCRC32(curValue);

            assertTrue(calculated1 == crc);
            assertTrue(calculated2 == crc);
            assertTrue(calculated3 == (int) (crc & 0x0FFFFFFFFL));
            assertTrue(calculated1b == crc);
            assertTrue(calculated2b == crc);
            assertTrue(calculated3b == (int) (crc & 0x0FFFFFFFFL));

            final long cv = curValue; // has to be final to be captured in a lambda below
            assertThrows(RuntimeException.class, () -> { tableDriven.finalCRC16(cv); });
            assertThrows(RuntimeException.class, () -> { tableDriven.finalCRC8(cv); });
        };

        doTest.apply(CRC.Parameters.CRC32, 0xCBF43926L, "123456789");
        doTest.apply(CRC.Parameters.CRC32, 0x906319F2L, "12345678901234567890");
        doTest.apply(CRC.Parameters.CRC32, 0x814F2B45L, "Introduction on CRC calculations");
        doTest.apply(CRC.Parameters.CRC32, 0x8F273817L, "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits.");

        doTest.apply(CRC.Parameters.Castagnoli, 0xE3069283L, "123456789");
        doTest.apply(CRC.Parameters.Castagnoli, 0xA8B4A6B9L, "12345678901234567890");
        doTest.apply(CRC.Parameters.Castagnoli, 0x54F98A9EL, "Introduction on CRC calculations");
        doTest.apply(CRC.Parameters.Castagnoli, 0x864FDAFCL, "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits.");

        doTest.apply(CRC.Parameters.Koopman, 0x2D3DD0AEL, "123456789");
        doTest.apply(CRC.Parameters.Koopman, 0xCC53DEACL, "12345678901234567890");
        doTest.apply(CRC.Parameters.Koopman, 0x1B8101F9L, "Introduction on CRC calculations");
        doTest.apply(CRC.Parameters.Koopman, 0xA41634B2L, "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits.");
    }

    interface TestFunction5<A, B, C, D, E> {
        public void apply(A a, B b, C c, D d, E e);
    }
    @Test
    public void testCalculateCRCWithOffsetAndLength() {
        TestFunction5<CRC.Parameters, Long, Integer, Integer, String> doTest = (crcParams, crc, offset, length, data) -> {
            byte[] dataBytes = data.getBytes();
            long calculated1 = CRC.calculateCRC(crcParams, dataBytes, offset, length);

            // same test using table driven
            CRC tableDriven = new CRC(crcParams);
            long calculated2 = tableDriven.calculateCRC(dataBytes, offset, length);

            then:
            assertTrue(calculated1 == crc);
            assertTrue(calculated2 == crc);
        };

        //               crcParams             crc       offset  length                   data
        doTest.apply(CRC.Parameters.CRC32, 0xCBF43926L, 0,  9, "123456789Introduction on CRC calculations12345678901234567890");
        doTest.apply(CRC.Parameters.CRC32, 0x906319F2L,41, 20, "123456789Introduction on CRC calculations12345678901234567890");
        doTest.apply(CRC.Parameters.CRC32, 0x814F2B45L, 9, 32, "123456789Introduction on CRC calculations12345678901234567890");
    }


    @Test
    public void testCRC64() {
        TestFunction3<CRC.Parameters, Long, String> doTest = (crcParams, crc , data) -> {
            byte[] dataBytes = data.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.wrap(dataBytes);

            long calculated1 = CRC.calculateCRC(crcParams, dataBytes);
            long calculated1b = CRC.calculateCRC(crcParams, byteBuffer);

            // same test using table driven
            CRC tableDriven = new CRC(crcParams);
            long calculated2 = tableDriven.calculateCRC(dataBytes);
            long calculated2b = tableDriven.calculateCRC(byteBuffer);

            // same test feeding data in chunks of different size
            long curValue = tableDriven.init();
            int start = 0;
            int step = 1;
            while (start < dataBytes.length) {
                int end = start + step;
                if (end > dataBytes.length) {
                    end = dataBytes.length;
                }
                curValue = tableDriven.update(curValue, dataBytes, start, end - start);
                start = end;
                step *= 2;
            }
            long calculated3 = tableDriven.finalCRC(curValue);

            // same test feeding data in chunks of different size from the byte buffer
            // (note that byte buffer has same length as the byte array and that is used in the code below)
            curValue = tableDriven.init();
            start = 0;
            step = 5;
            while (start < dataBytes.length) {
                int end = start + step;
                if (end > dataBytes.length) {
                    end = dataBytes.length;
                }
                curValue = tableDriven.update(curValue, byteBuffer, start, end - start);
                start = end;
                step *= 2;
            }
            long calculated3b = tableDriven.finalCRC(curValue);

            then:
            assertTrue(calculated1 == crc);
            assertTrue(calculated2 == crc);
            assertTrue(calculated3 == crc);
            assertTrue(calculated1b == crc);
            assertTrue(calculated2b == crc);
            assertTrue(calculated3b == crc);


            final long cv = curValue; // has to be final to be captured in a lambda below
            assertThrows(RuntimeException.class, () -> { tableDriven.finalCRC32(cv); });
            assertThrows(RuntimeException.class, () -> { tableDriven.finalCRC16(cv); });
            assertThrows(RuntimeException.class, () -> { tableDriven.finalCRC8(cv); });

        };

        doTest.apply(CRC.Parameters.CRC64ISO, 0xB90956C775A41001L, "123456789");
        doTest.apply(CRC.Parameters.CRC64ISO, 0x8DB93749FB37B446L, "12345678901234567890");
        doTest.apply(CRC.Parameters.CRC64ISO, 0xBAA81A1ED1A9209BL, "Introduction on CRC calculations");
        doTest.apply(CRC.Parameters.CRC64ISO, 0x347969424A1A7628L, "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits.");

        doTest.apply(CRC.Parameters.CRC64ECMA, 0x995DC9BBDF1939FAL, "123456789");
        doTest.apply(CRC.Parameters.CRC64ECMA, 0x0DA1B82EF5085A4AL, "12345678901234567890");
        doTest.apply(CRC.Parameters.CRC64ECMA, 0xCF8C40119AE90DCBL, "Introduction on CRC calculations");
        doTest.apply(CRC.Parameters.CRC64ECMA, 0x31610F76CFB272A5L, "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits.");
    }

}
