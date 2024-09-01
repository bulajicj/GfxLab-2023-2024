package xyz.marsavic.gfxlab.graphics3d.scenes;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Color;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.*;
import xyz.marsavic.gfxlab.graphics3d.solids.Ball;
import xyz.marsavic.gfxlab.graphics3d.solids.Group;
import xyz.marsavic.gfxlab.graphics3d.solids.HalfSpace;
import xyz.marsavic.gfxlab.graphics3d.textures.Grid;
import xyz.marsavic.gfxlab.graphics3d.textures.ImageTexture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BallScene extends Scene.Base {

    public BallScene() {

        // Material setup for walls and floor
        var materialUVWalls  = Grid.standard(Color.WHITE);
        var materialUVWallsL = Grid.standard(Color.hsb(0.00, 0.5, 1.0));
        var materialUVWallsR = Grid.standard(Color.hsb(0.33, 0.5, 1.0));
        var floorTexture = new ImageTexture("/images/textures/wooden-floor.jpg");
        if (floorTexture == null) {
            throw new RuntimeException("Texture could not be loaded. Check file path: /images/textures/wooden-floor.jpg");
        }


        Collection<Solid> solids = new ArrayList<>();
        Collections.addAll(solids,
                HalfSpace.pn(Vec3.xyz(-1,  0,  0), Vec3.xyz( 1,  0,  0), materialUVWallsL),
                HalfSpace.pn(Vec3.xyz( 1,  0,  0), Vec3.xyz(-1,  0,  0), materialUVWallsR),
                HalfSpace.pn(Vec3.xyz( 0, -1,  0), Vec3.xyz( 0,  1,  0), floorTexture),
                HalfSpace.pn(Vec3.xyz( 0,  1,  0), Vec3.xyz( 0, -1,  0), Material.LIGHT),
                HalfSpace.pn(Vec3.xyz( 0,  0,  1), Vec3.xyz( 0,  0, -1), materialUVWalls)
        );

        // Load the textures
        //var texture = new ImageTexture("/images/textures/blue-texture.jpg");
        //var texture = new ImageTexture("/images/textures/denim-texture.jpg");
        //var texture = new ImageTexture("/images/textures/brick-wall.jpg");
        var texture = new ImageTexture("/images/textures/denim-texture.jpg");
        if (texture == null) {
            throw new RuntimeException("Normal map could not be loaded. Check file path: /images/textures/denim-texture.jpg");
        }

        //Load the normalMaps
        //ImageTexture normalMapTexture = new ImageTexture("/images/normalMaps/NormalMap3.jpg");
        //ImageTexture normalMapTexture = new ImageTexture("/images/normalMaps/NormalMap6.jpg");
        ImageTexture normalMapTexture = new ImageTexture("/images/normalMaps/NormalMap.jpg");
        if (normalMapTexture == null) {
            throw new RuntimeException("Normal map could not be loaded. Check file path: /images/normalMaps/NormalMap.jpg");
        }

        //Applying textures with normal maps
        F1<Material, Vector> material = (Vector uv) -> {
            Material baseMaterial = texture.at(uv);

            // Return a material that combines both texture and the normal map
            return baseMaterial.normalMap(normalMapTexture);
        };

        Ball ball = Ball.cr(Vec3.ZERO, 0.7, material);


        // Just ball with normal map being applied on it
      /* Ball ball = Ball.cr(Vec3.ZERO, 0.7, uv ->
            Material.matte(Color.WHITE)
                    .shininess(32)
                    .normalMap(normalMapTexture)
       );*/

        solids.add(ball);

        this.solid = Group.of(solids);

        // Lighting setup
        Collections.addAll(lights,
                Light.pc(Vec3.xyz(-1, 1, 1), Color.WHITE),
                Light.pc(Vec3.xyz(2, 1, 2), Color.rgb(1.0, 0.5, 0.5)),
                Light.pc(Vec3.xyz(0, 0, -1), Color.gray(0.2))
        );
    }
}
