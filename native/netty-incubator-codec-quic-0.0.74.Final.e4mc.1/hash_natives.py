import struct

while True:
    (digest, filename) = input().strip().split()
    digest = list(map(lambda byte: struct.unpack("b", bytes([byte]))[0], bytes.fromhex(digest)))
    listed = str(digest).replace("[", "{").replace("]", "}")
    print(f'case "{filename}":\n    return new byte[]{listed};')