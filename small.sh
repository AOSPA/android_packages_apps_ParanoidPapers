size=768x432
number=0

while [[ number != 14 ]]; do
  convert res/drawable-nodpi/wallpaper_"$number".jpg -resize $size res/drawable-nodpi/wallpaper_"$number"_small.jpg
  number=$(( number + 1 ))
done
