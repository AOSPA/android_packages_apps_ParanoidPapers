import os

from PIL import Image

path = os.path.dirname(os.path.realpath(__file__))

wallpapers_path = path + "/res/drawable-nodpi/"

wallpapers = os.listdir(wallpapers_path)

for wallpaper in wallpapers:
    wallpaper_path = wallpapers_path + wallpaper

    # Get rid of existing _small variants
    if "_small.jpg" in str(wallpaper):
        os.remove(wallpaper_path)
        continue

    # Append _small.jpg to the wallpaper
    wallpaper_small = wallpaper[:-4] + "_small.jpg"
    wallpaper_small_path = wallpapers_path + wallpaper_small


    # Save the wallpaper with 1/5 size to wallpaper_small_path
    with Image.open(wallpaper_path) as img:
        small_width = img.width / 5
        small_height = img.height / 5

        size = int(small_width), int(small_height)

        img_small = img.resize(size, Image.ANTIALIAS)
        img_small.save(wallpaper_small_path, "JPEG")
