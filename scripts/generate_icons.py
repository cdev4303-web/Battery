import zlib
import struct
import math
import os

def create_battery_icon_png(width, height, filepath, maskable=False):
    # Generates a PNG with dark slate background, glowing emerald battery, metallic cap, and golden lightning bolt
    raw_data = bytearray()
    
    cx, cy = width / 2.0, height / 2.0
    scale = width / 512.0
    
    # Dimensions scaled
    pad = 0.12 if maskable else 0.05
    corner_r = 0.22 if not maskable else 0.0
    
    for y in range(height):
        row = bytearray([0]) # filter type 0
        py = y / scale
        for x in range(width):
            px = x / scale
            
            # Default background: Dark gradient
            dist_center = math.hypot(px - 256, py - 256) / 362.0
            r = int(max(2, min(18, 14 * (1 - dist_center))))
            g = int(max(6, min(40, 36 * (1 - dist_center) + 8)))
            b = int(max(23, min(65, 55 * (1 - dist_center) + 15)))
            a = 255
            
            # Rounded outer card if not maskable
            if not maskable:
                dx = max(0, abs(px - 256) - (256 - 48))
                dy = max(0, abs(py - 256) - (256 - 48))
                if math.hypot(dx, dy) > 48:
                    r, g, b, a = 0, 0, 0, 0
            
            if a > 0:
                # Battery top terminal cap: x: [216, 296], y: [60, 88]
                if 216 <= px <= 296 and 60 <= py <= 88:
                    # Metallic silver highlight
                    fx = (px - 216) / 80.0
                    metallic = int(140 + 80 * math.sin(fx * math.pi))
                    r, g, b = metallic, metallic + 10, metallic + 20
                
                # Outer battery body: x: [136, 376], y: [88, 448] (rx: 32)
                bx = max(0, abs(px - 256) - (120 - 28))
                by = max(0, abs(py - 268) - (180 - 28))
                if math.hypot(bx, by) <= 28:
                    # Inside battery body
                    # Border check
                    if math.hypot(bx, by) >= 20:
                        # Body casing dark slate border
                        r, g, b = 51, 65, 85
                    else:
                        # Inside chamber: Glass with emerald green gradient
                        fill_level_y = 160  # ~80% full
                        if py >= fill_level_y:
                            # Glowing emerald liquid
                            fy = (py - fill_level_y) / (448 - fill_level_y)
                            r = int(16 + 20 * fy)
                            g = int(185 - 60 * fy)
                            b = int(129 - 50 * fy)
                            # Light vertical reflection on left
                            if 158 <= px <= 170:
                                r = min(255, r + 70)
                                g = min(255, g + 70)
                                b = min(255, b + 70)
                        else:
                            # Empty chamber top
                            r, g, b = 15, 23, 42
                
                # Lightning bolt in center
                # Triangle 1: (276, 160) -> (208, 276) -> (260, 276)
                # Triangle 2: (244, 276) -> (306, 240) -> (234, 368) -> (264, 276)
                # Simple point-in-polygon check for lightning bolt
                bolt = [
                    (276, 160), (206, 276), (256, 276),
                    (236, 368), (312, 246), (266, 246)
                ]
                # Ray casting for bolt polygon
                inside_bolt = False
                j = len(bolt) - 1
                for i in range(len(bolt)):
                    xi, yi = bolt[i]
                    xj, yj = bolt[j]
                    if ((yi > py) != (yj > py)) and (px < (xj - xi) * (py - yi) / (yj - yi) + xi):
                        inside_bolt = not inside_bolt
                    j = i
                
                if inside_bolt:
                    # Gold-amber gradient with white highlight
                    r, g, b = 254, 224, 60
                    if abs(px - 260) < 6:
                        r, g, b = 255, 255, 220
            
            row.extend([r, g, b, a])
        raw_data.extend(row)
        
    compressed = zlib.compress(raw_data)
    
    with open(filepath, 'wb') as f:
        f.write(b'\x89PNG\r\n\x1a\n') # Header
        # IHDR chunk
        ihdr = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)
        f.write(struct.pack('>I', len(ihdr)) + b'IHDR' + ihdr + struct.pack('>I', zlib.crc32(b'IHDR' + ihdr)))
        # IDAT chunk
        f.write(struct.pack('>I', len(compressed)) + b'IDAT' + compressed + struct.pack('>I', zlib.crc32(b'IDAT' + compressed)))
        # IEND chunk
        f.write(struct.pack('>I', 0) + b'IEND' + struct.pack('>I', zlib.crc32(b'IEND')))

os.makedirs('public', exist_ok=True)
create_battery_icon_png(512, 512, 'public/pwa-512x512.png', maskable=False)
create_battery_icon_png(192, 192, 'public/pwa-192x192.png', maskable=False)
create_battery_icon_png(512, 512, 'public/pwa-maskable-512x512.png', maskable=True)

# Also copy to Android mipmap/drawables
os.makedirs('app/src/main/res/drawable-nodpi', exist_ok=True)
create_battery_icon_png(512, 512, 'app/src/main/res/drawable-nodpi/app_icon.png', maskable=False)
print("Icons generated successfully!")
