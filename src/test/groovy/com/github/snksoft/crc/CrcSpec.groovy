package com.github.snksoft.crc;

import spock.lang.Specification

class CrcSpec extends Specification{

    private static final longText = "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits."

    private static byte[] testArray = null

    static {
        testArray = new byte[256];
        for (int i=0; i<testArray.length; i++)
        {
            testArray[i] = (byte)(i&0x0FF)
        }
    }

    def "Test CRC polynomials of various widths"() {
        when:
            byte[] dataBytes = data instanceof byte[] ? data : data.getBytes()
            long calculated1 = CRC.calculateCRC(crcParams, dataBytes)
        then:
            calculated1 == crc
        when:
            CRC table = new CRC(crcParams)
            long tableBasedCrc = table.calculateCRC(dataBytes)
        then:
            tableBasedCrc == crc

        where:
            crcParams                                                | crc  | data
            new CRC.Parameters(3, 0x03, 0x00, false, false, 0x7) | 0x04 | "123456789" // CRC-3/GSM
            new CRC.Parameters(3, 0x03, 0x00, false, false, 0x7) | 0x06 | longText
            new CRC.Parameters(3, 0x03, 0x00, false, false, 0x7) | 0x02 | testArray
            new CRC.Parameters(3, 0x03, 0x07, true,  true,  0x0) | 0x06 | "123456789" // CRC-3/ROHC
            new CRC.Parameters(3, 0x03, 0x07, true,  true,  0x0) | 0x03 |longText
            new CRC.Parameters(4, 0x03, 0x00, true,  true,  0x0) | 0x07 | "123456789" // CRC-4/ITU
            new CRC.Parameters(4, 0x03, 0x0f, false, false, 0xf) | 0x0b | "123456789" // CRC-4/INTERLAKEN
            new CRC.Parameters(4, 0x03, 0x0f, false, false, 0xf) | 0x01 | longText // CRC-4/INTERLAKEN
            new CRC.Parameters(4, 0x03, 0x0f, false, false, 0xf) | 0x07 | testArray // CRC-4/INTERLAKEN
            new CRC.Parameters(5, 0x09, 0x09, false, false, 0x0) | 0x00 | "123456789" // CRC-5/EPC
            new CRC.Parameters(5, 0x15, 0x00, true,  true,  0x0) | 0x07 | "123456789" // CRC-5/ITU
            new CRC.Parameters(6, 0x27, 0x3f, false, false, 0x0) | 0x0d | "123456789" // CRC-6/CDMA2000-A
            new CRC.Parameters(6, 0x07, 0x3f, false, false, 0x0) | 0x3b | "123456789" // CRC-6/CDMA2000-B
            new CRC.Parameters(6, 0x07, 0x3f, false, false, 0x0) | 0x24 | testArray // CRC-6/CDMA2000-B
            new CRC.Parameters(7, 0x09, 0x00, false, false, 0x0) | 0x75 | "123456789" // CRC-7
            new CRC.Parameters(7, 0x09, 0x00, false, false, 0x0) | 0x78 | testArray // CRC-7
            new CRC.Parameters(7, 0x4f, 0x7f, true,  true,  0x0) | 0x53 | "123456789" // CRC-7/ROHC

            new CRC.Parameters(12, 0xd31, 0x00, false, false, 0xfff) | 0x0b34 | "123456789" // CRC-12/GSM
            new CRC.Parameters(12, 0x80f, 0x00, false, true, 0x00) | 0x0daf | "123456789" // CRC-12/UMTS
            new CRC.Parameters(13, 0x1cf5, 0x00, false, false, 0x00) | 0x04fa | "123456789" // CRC-13/BBC
            new CRC.Parameters(14, 0x0805, 0x00, true, true, 0x00) | 0x082d | "123456789" // CRC-14/DARC
            new CRC.Parameters(14, 0x202d, 0x00, false, false, 0x3fff) | 0x30ae | "123456789" // CRC-14/GSM

            new CRC.Parameters(15, 0x4599, 0x00, false, false, 0x00) | 0x059e | "123456789" // CRC-15
            new CRC.Parameters(15, 0x4599, 0x00, false, false, 0x00) | 0x2857 | longText
            new CRC.Parameters(15, 0x6815, 0x00, false, false, 0x0001) | 0x2566 | "123456789" // CRC-15/MPT1327

            new CRC.Parameters(21, 0x102899, 0x000000, false, false, 0x000000) | 0x0ed841 | "123456789" // CRC-21/CAN-FD
            new CRC.Parameters(24, 0x864cfb, 0xb704ce, false, false, 0x000000) | 0x21cf02 | "123456789" // CRC-24
            new CRC.Parameters(24, 0x5d6dcb, 0xfedcba, false, false, 0x000000) | 0x7979bd | "123456789" // CRC-24/FLEXRAY-A
            new CRC.Parameters(31, 0x04c11db7, 0x7fffffff, false, false, 0x7fffffff) | 0x0ce9e46c | "123456789" // CRC-31/PHILIPS
    }

    def "CRC8 Tests"() {

        when:
            byte[] dataBytes = data.getBytes()
            long calculated1 = CRC.calculateCRC(crcParams, dataBytes)

            // same test using table driven
            CRC tableDriven = new CRC(crcParams)
            long calculated2 = tableDriven.calculateCRC(dataBytes)

            // same test feeding data in chunks of different size
            long curValue = tableDriven.init()
            int start = 0
            int step = 1
            while (start < dataBytes.length)
            {
                int end = start + step
                if (end > dataBytes.length)
                {
                    end = dataBytes.length
                }
                curValue = tableDriven.update(curValue, dataBytes, start, end-start)
                start = end
                step *= 2
            }
            byte calculated3 = tableDriven.finalCRC8(curValue)

        then:
            calculated1 == crc
            calculated2 == crc
            calculated3 == (byte)(crc & 0x00FF)

        where:
            crcParams                                       |  crc | data
            new CRC.Parameters(8, 0x07, 0, false, false, 0) | 0xf4 | "123456789"
    }

    def "CRC16 Tests"() {

        when:
            byte[] dataBytes = data.getBytes()
            long calculated1 = CRC.calculateCRC(crcParams, dataBytes)

            // same test using table driven
            CRC tableDriven = new CRC(crcParams)
            long calculated2 = tableDriven.calculateCRC(dataBytes)

            // same test feeding data in chunks of different size
            long curValue = tableDriven.init()
            int start = 0
            int step = 1
            while (start < dataBytes.length)
            {
                int end = start + step
                if (end > dataBytes.length)
                {
                    end = dataBytes.length
                }
                curValue = tableDriven.update(curValue, dataBytes, start, end-start)
                start = end
                step *= 2
            }
            short calculated3 = tableDriven.finalCRC16(curValue)

        then:
            calculated1 == crc
            calculated2 == crc
            calculated3 == (short)(crc & 0x0000FFFF)
    
        where:
            crcParams              |  crc   | data
            CRC.Parameters.CCITT   | 0x29B1 | "123456789"
            CRC.Parameters.CCITT   | 0xDA31 | "12345678901234567890"
            CRC.Parameters.CCITT   | 0xC87E | "Introduction on CRC calculations"
            CRC.Parameters.CCITT   | 0xD6ED | "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits."

            CRC.Parameters.XMODEM  | 0x31C3 | "123456789"
            CRC.Parameters.XMODEM  | 0x2C89 | "12345678901234567890"
            CRC.Parameters.XMODEM  | 0x3932 | "Introduction on CRC calculations"
            CRC.Parameters.XMODEM  | 0x4E86 | "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits."

            CRC.Parameters.XMODEM2 | 0x0C73 | "123456789"
            CRC.Parameters.XMODEM2 | 0x122E | "12345678901234567890"
            CRC.Parameters.XMODEM2 | 0x0638 | "Introduction on CRC calculations"
            CRC.Parameters.XMODEM2 | 0x187A | "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits."
    }


    def "CRC32 Tests"() {

        when:
            byte[] dataBytes = data.getBytes()
            long calculated1 = CRC.calculateCRC(crcParams, dataBytes)

            // same test using table driven
            CRC tableDriven = new CRC(crcParams)
            long calculated2 = tableDriven.calculateCRC(dataBytes)

            // same test feeding data in chunks of different size
            long curValue = tableDriven.init()
            int start = 0
            int step = 1
            while (start < dataBytes.length)
            {
                int end = start + step
                if (end > dataBytes.length)
                {
                    end = dataBytes.length
                }
                curValue = tableDriven.update(curValue, dataBytes, start, end-start)
                start = end
                step *= 2
            }
            short calculated3 = tableDriven.finalCRC32(curValue)

        then:
            calculated1 == crc
            calculated2 == crc
            calculated3 == (short)(crc & 0x0000FFFF)

        when:
            tableDriven.finalCRC16(curValue)
        then:
            thrown RuntimeException
        when:
            tableDriven.finalCRC8(curValue)
        then:
            thrown RuntimeException

        where:
            crcParams                 |  crc       | data
            CRC.Parameters.CRC32      | 0xCBF43926 | "123456789"
            CRC.Parameters.CRC32      | 0x906319F2 | "12345678901234567890"
            CRC.Parameters.CRC32      | 0x814F2B45 | "Introduction on CRC calculations"
            CRC.Parameters.CRC32      | 0x8F273817 | "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits."

            CRC.Parameters.Castagnoli | 0xE3069283 | "123456789"
            CRC.Parameters.Castagnoli | 0xA8B4A6B9 | "12345678901234567890"
            CRC.Parameters.Castagnoli | 0x54F98A9E | "Introduction on CRC calculations"
            CRC.Parameters.Castagnoli | 0x864FDAFC | "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits."

            CRC.Parameters.Koopman    | 0x2D3DD0AE | "123456789"
            CRC.Parameters.Koopman    | 0xCC53DEAC | "12345678901234567890"
            CRC.Parameters.Koopman    | 0x1B8101F9 | "Introduction on CRC calculations"
            CRC.Parameters.Koopman    | 0xA41634B2 | "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits."
    }

    def "CRC64 Tests"() {

        when:
            byte[] dataBytes = data.getBytes()
            long calculated1 = CRC.calculateCRC(crcParams, dataBytes)

            // same test using table driven
            CRC tableDriven = new CRC(crcParams)
            long calculated2 = tableDriven.calculateCRC(dataBytes)

            // same test feeding data in chunks of different size
            long curValue = tableDriven.init()
            int start = 0
            int step = 1
            while (start < dataBytes.length)
            {
                int end = start + step
                if (end > dataBytes.length)
                {
                    end = dataBytes.length
                }
                curValue = tableDriven.update(curValue, dataBytes, start, end-start)
                start = end
                step *= 2
            }
            long calculated3 = tableDriven.finalCRC(curValue)

        then:
            calculated1 == crc
            calculated2 == crc
            calculated3 == crc

        when:
            tableDriven.finalCRC32(curValue)
        then:
            thrown RuntimeException
        when:
            tableDriven.finalCRC16(curValue)
        then:
            thrown RuntimeException
        when:
            tableDriven.finalCRC8(curValue)
        then:
            thrown RuntimeException

        where:
            crcParams                |  crc               | data
            CRC.Parameters.CRC64ISO  | 0xB90956C775A41001L | "123456789"
//            CRC.Parameters.CRC64ISO  | 0x8DB93749FB37B446L | "12345678901234567890"
//            CRC.Parameters.CRC64ISO  | 0xBAA81A1ED1A9209BL | "Introduction on CRC calculations"
//            CRC.Parameters.CRC64ISO  | 0x347969424A1A7628L | "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits."
//
//            CRC.Parameters.CRC64ECMA | 0x995DC9BBDF1939FAL | "123456789"
//            CRC.Parameters.CRC64ECMA | 0x0DA1B82EF5085A4AL | "12345678901234567890"
//            CRC.Parameters.CRC64ECMA | 0xCF8C40119AE90DCBL | "Introduction on CRC calculations"
//            CRC.Parameters.CRC64ECMA | 0x31610F76CFB272A5L | "Whenever digital data is stored or interfaced, data corruption might occur. Since the beginning of computer science, people have been thinking of ways to deal with this type of problem. For serial data they came up with the solution to attach a parity bit to each sent byte. This simple detection mechanism works if an odd number of bits in a byte changes, but an even number of false bits in one byte will not be detected by the parity check. To overcome this problem people have searched for mathematical sound mechanisms to detect multiple false bits."
    }

}
