package com.github.snksoft.crc;

import spock.lang.Specification

class CrcSpec extends Specification{

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
