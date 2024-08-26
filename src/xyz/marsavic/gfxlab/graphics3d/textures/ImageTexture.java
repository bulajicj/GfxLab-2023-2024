package xyz.marsavic.gfxlab.graphics3d.textures;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Material;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageTexture implements F1<Material, Vector> {

    Color[][] colors;

    public ImageTexture(String fileName){
        this.colors = loadTexture(fileName);
        if (this.colors == null) {
            throw new RuntimeException("Failed to load texture from a file: " + fileName);
        }
    }

    @Override
    public Material at(Vector uv) {
        double x = Math.abs(uv.x() % 1);
        double y = Math.abs(uv.y() % 1);
        int u = (int) (y * colors.length);
        int v = (int) (x * colors[0].length);

        u = Math.min(u, colors.length - 1);
        v = Math.min(v, colors[0].length - 1);

        return Material.matte(colors[u][v]);
    }

    private Color[][] loadTexture(String fn) {
        BufferedImage img;
        try {
            InputStream is = getClass().getResourceAsStream(fn);
            img = ImageIO.read(is);

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        int w = img.getWidth(), h = img.getHeight();

        Color[][] pixels = new Color[h][w];

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                pixels[y][x] = Color.code(img.getRGB(x,y));

        return pixels;
    }
    // Sample the normal map to get a normal vector
    public Vec3 sampleNormal(Vector uv) {
        double x = Math.abs(uv.x() % 1);
        double y = Math.abs(uv.y() % 1);
        int u = (int) (y * colors.length);
        int v = (int) (x * colors[0].length);
        Color color = colors[u][v];

        // Convert the color to a normal vector
        double nx = color.getR() * 2.0 - 1.0;  // Red channel as X component
        double ny = color.getG() * 2.0 - 1.0;  // Green channel as Y component
        double nz = color.getB() * 2.0 - 1.0;  // Blue channel as Z component

        return Vec3.xyz(nx, ny, nz).normalized_();
    }
}
