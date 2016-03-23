
package com.vurtnewk.emsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理socket收到的数据流，按照规则解析成
 * 
 * @author liangc
 */
class PacketDecoder {

    public static void main(String[] args) {
        List<Byte> a = new ArrayList<Byte>();
        a.add((byte) 97);
        a.add((byte) 97);
        a.add((byte) 97);

        List<Byte> b = new ArrayList<Byte>();
        b.addAll(a);
        a.clear();
        System.out.println(b);
    }

    /**
     * @param list 字节流集
     * @param split 分割
     * @param packetList 被分割出的有序包集合
     * @param new_part 本次产生的一个不完整的包的前半段
     * @param part 上一次遗留的不完整的包的前半?
     * @return
     */
    public static void splitByteArray(List<Byte> list, String split, List<String> packetList,
            List<Byte> new_part, List<Byte> part) {
        splitByteArray(list, split, packetList, new_part, part, 0);
    }

    public static void splitByteArray(List<Byte> list, String split, List<String> packetList,
            List<Byte> new_part, List<Byte> part, int counter) {
        byte _split = split.getBytes()[0];
        if (list.size() < 1) {
            return;
        }
        int index = list.indexOf(_split);
        if (index == 0) {
            list.remove(0);
            splitByteArray(list, split, packetList, new_part, part, ++counter);
        } else if (index == -1) {
            if (list.size() > 0) {
                for (byte b : list) {
                    new_part.add(b);
                }
            }
        } else {
            List<Byte> p = list.subList(0, index);
            String buff = null;
            if (counter == 0 && part.size() > 0) {
                for (byte b : p) {
                    part.add(b);
                }
                buff = binaryListToString(part);
            } else {
                buff = binaryListToString(p);
            }
            if (buff != null) {
                packetList.add(buff);
            }
            if (list.size() - 1 != index) {
                List<Byte> next = list.subList(index, list.size());
                splitByteArray(next, split, packetList, new_part, part, ++counter);
            }
        }
    }

    /**
     * @param p
     * @return
     */
    public static String binaryListToString(List<Byte> p) {

        byte[] buff = new byte[p.size()];
        for (int i = 0; i < p.size(); i++) {
            byte o = p.get(i);
            buff[i] = o;
        }
        return new String(buff);
    }

    /**
     * @param bin
     * @return
     */
    public static List<Byte> parseBinaryList(byte[] bin, int len) {
        List<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < len; i++) {
            list.add(bin[i]);
        }
        return list;
    }

}
