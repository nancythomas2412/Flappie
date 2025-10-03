# ❄️ Bird Skin Assets Specification for Glacier Bird Game

## Asset Requirements

### **Technical Specifications**
- **Format:** PNG with transparency
- **Resolution:** 128x128px (base size), provide 2x and 3x versions
- **Color Mode:** RGBA (32-bit with alpha channel)
- **Style:** Consistent with ice cave and arctic bird aesthetic
- **Animation Frames:** 5 frames per skin (neutral, transition, wingsUp, wingsDown, sad)

### **File Naming Convention**
```
bird_[skinname]_[frame].png

Examples:
- bird_golden_neutral.png
- bird_golden_wings_up.png  
- bird_golden_wings_down.png
- bird_golden_transition.png
- bird_golden_sad.png
```

## **Bird Skin Designs Needed**

### **1. Golden Bird ($1.99) - Rare**
- **Base Color:** Bright gold (#FFD700)
- **Effects:** Subtle sparkle particles, metallic sheen
- **Personality:** Luxurious, elegant
- **Wings:** Gradient gold to yellow
- **Beak:** Orange-gold (#FFA500)

### **2. Rainbow Bird ($2.49) - Epic**  
- **Base Color:** Prismatic rainbow gradient
- **Effects:** Color-shifting trail, rainbow sparkles
- **Personality:** Vibrant, magical
- **Wings:** Each feather different color
- **Beak:** Bright pink (#FF69B4)

### **3. Ninja Bird ($2.49) - Epic**
- **Base Color:** Dark purple/black (#2C1810)
- **Effects:** Smoke trail, stealth shimmer
- **Personality:** Mysterious, stealthy
- **Wings:** Dark with silver edges
- **Beak:** Dark gray (#555555)
- **Special:** Small ninja mask/headband

### **4. Cyber Bird ($3.99) - Legendary**
- **Base Color:** Neon blue (#00FFFF) with circuit patterns
- **Effects:** Digital glitch particles, LED glow
- **Personality:** Futuristic, high-tech
- **Wings:** Holographic with circuit lines
- **Beak:** Metallic silver (#C0C0C0)
- **Special:** Cyber helmet/visor

### **5. Phoenix Bird ($3.99) - Legendary**
- **Base Color:** Fiery red-orange gradient (#FF4500 to #FFD700)
- **Effects:** Fire trail, ember particles
- **Personality:** Majestic, powerful
- **Wings:** Flame-like feathers
- **Beak:** Golden yellow (#FFD700)
- **Special:** Small flame crest

### **6. Crystal Bird ($2.99) - Epic**
- **Base Color:** Transparent crystal with prismatic reflections
- **Effects:** Light refraction, crystal sparkles  
- **Personality:** Ethereal, pure
- **Wings:** Semi-transparent with rainbow edges
- **Beak:** Diamond-like clarity
- **Achievement Unlock:** Score 1000+ points

### **7. Shadow Bird ($1.99) - Rare**
- **Base Color:** Deep black with purple highlights (#1A1A2E)
- **Effects:** Dark smoke trail, shadowy afterimages
- **Personality:** Mysterious, edgy
- **Wings:** Translucent black
- **Beak:** Dark purple (#4B0082)
- **Achievement Unlock:** Die 50+ times

### **8. Valentine Bird ($1.99) - Rare**
- **Base Color:** Pink and red with heart patterns (#FF69B4)
- **Effects:** Heart particle trail, love sparkles
- **Personality:** Cute, romantic
- **Wings:** Pink with red heart spots
- **Beak:** Bright red (#FF0000)
- **Seasonal:** February release

## **Asset Creation Options**

### **Option 1: Commission Artist (Recommended)**
- **Platform:** Fiverr, Upwork, 99designs
- **Budget:** $200-500 for all skins
- **Timeline:** 1-2 weeks
- **Quality:** Professional, consistent style

### **Option 2: AI Art Generation**
- **Tools:** Midjourney, DALL-E, Stable Diffusion
- **Budget:** $50-100 for subscriptions
- **Timeline:** 1-3 days
- **Process:** Generate base designs, touch up in Photoshop

### **Option 3: Asset Store**
- **Platforms:** Unity Asset Store, GameDev Market
- **Budget:** $100-300
- **Timeline:** Immediate
- **Customization:** May need color/style modifications

### **Option 4: In-House Creation**
- **Tools:** Photoshop, Procreate, Aseprite
- **Budget:** Software cost only
- **Timeline:** 1-2 weeks
- **Skill Required:** Digital art experience

## **Implementation Steps**

### **1. Asset Preparation**
```
app/src/main/res/drawable-hdpi/     (72dpi)
app/src/main/res/drawable-xhdpi/    (96dpi)  
app/src/main/res/drawable-xxhdpi/   (144dpi)
app/src/main/res/drawable-xxxhdpi/  (192dpi)
```

### **2. Resource Manager Update**
Update `ResourceManager.kt` to load actual skin sprites:
```kotlin
when (skinType) {
    BirdSkinType.GOLDEN -> loadBirdSkinSprites("golden")
    BirdSkinType.RAINBOW -> loadBirdSkinSprites("rainbow")
    // ... etc
}
```

### **3. Testing Checklist**
- [ ] All skins load correctly
- [ ] Animations play smoothly  
- [ ] Memory usage is acceptable
- [ ] Visual consistency maintained
- [ ] Performance impact minimal

## **Current Status**
✅ **Placeholder system working**  
✅ **Skin framework implemented**
✅ **Purchase system ready**
⏳ **Need actual graphics assets**

## **Priority Order**
1. **Golden Bird** (highest revenue potential)
2. **Rainbow Bird** (most popular aesthetic)  
3. **Cyber Bird** (premium tier)
4. **Phoenix Bird** (premium tier)
5. **Ninja Bird** (popular theme)
6. **Crystal, Shadow, Valentine** (specialty skins)

## **Success Metrics**
- **Revenue Target:** $2-5 per user from skin purchases
- **Conversion Rate:** 15-25% of players purchase at least one skin
- **Collection Rate:** 40% of purchasers buy multiple skins

**Estimated Development Time:** 1-2 weeks once assets are ready
**Estimated Revenue Impact:** +200-400% from cosmetic purchases