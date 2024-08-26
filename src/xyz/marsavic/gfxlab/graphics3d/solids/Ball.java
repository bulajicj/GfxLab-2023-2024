package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.functions.F1;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.gfxlab.Vec3;
import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Material;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;
import xyz.marsavic.gfxlab.graphics3d.textures.ImageTexture;


public class Ball implements Solid {
	
	private final Vec3 c;
	private final double r;
	private final boolean inverted;
	private final F1<Material, Vector> mapMaterial;
	
	// transient
	private final double rSqr;
	
	
	/** Negative r will make the ball inverted (the resulting solid is a complement of a ball). */
	private Ball(Vec3 c, double r, F1<Material, Vector> mapMaterial) {
		this.c = c;
		this.r = r;
		this.mapMaterial = mapMaterial;
		rSqr = r * r;
		inverted = r < 0;
	}
	
	
	public static Ball cr(Vec3 c, double r, F1<Material, Vector> mapMaterial) {
		return new Ball(c, r, mapMaterial);
	}
	
	public static Ball cr(Vec3 c, double r) {
		return cr(c, r, Material.DEFAULT);
	}
	
	
	public Vec3 c() {
		return c;
	}
	
	
	public double r() {
		return r;
	}
	
	
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		Vec3 e = c().sub(ray.p());                                // Vector from the ray origin to the ball center
		
		double dSqr = ray.d().lengthSquared();
		double l = e.dot(ray.d()) / dSqr;
		double mSqr = l * l - (e.lengthSquared() - rSqr) / dSqr;
		
		if (mSqr > 0) {
			double m = Math.sqrt(mSqr);
			if (l - m > afterTime) return new HitBall(ray, l - m);
			if (l + m > afterTime) return new HitBall(ray, l + m);
		}
		return Hit.AtInfinity.axisAligned(ray.d(), inverted);
	}
	
	
	class HitBall extends Hit.RayT {
		
		protected HitBall(Ray ray, double t) {
			super(ray, t);
		}
		
		/*@Override
		public Vec3 n() {
			return ray().at(t()).sub(c());
		}*/

		/*@Override
		public Vec3 n_() {
			return n().div(r);
		}*/

		// Method to get the normal from the normal map if available
		@Override
		public Vec3 n_() {
			// This is the geometric normal at the hit point on the sphere.
			Vec3 normal = n();//ray().at(t()).sub(c()).div(r).normalized_();

			// Compute UV coordinates directly from the normal
			Vector uv = computeUV(normal);

			// Sample the normal map using the UV coordinates
			Vec3 normalMapSample = getNormalFromTexture(uv);

			// The sampled normal map is typically in tangent space, so we need to transform it
			// to world space. For this, we calculate the tangent and bitangent vectors.
			Vec3 tangent = Vec3.xyz(-normal.z(), 0, normal.x()).normalized_(); // Tangent vector
			Vec3 bitangent = normal.cross(tangent).normalized_(); // Bitangent vector

			// Convert the normal map sample from tangent space to world space
			return tangent.mul(normalMapSample.x())
					.add(bitangent.mul(normalMapSample.y()))
					.add(normal.mul(normalMapSample.z()))
					.normalized_();

		}

		private Vec3 getNormalFromTexture(Vector uv) {

			Material material = mapMaterial.at(uv);
			ImageTexture normalMapTexture = material.normalMap();

			if (normalMapTexture != null) {
				return normalMapTexture.sampleNormal(uv);
			} else {
				// Return the geometric normal if no normal map is present
				return Vec3.EZ;
			}
		}

		// Helper method to compute UV coordinates directly from the normal
		private Vector computeUV(Vec3 normal) {
			double u = 0.5 + Math.atan2(normal.z(), normal.x()) / (2 * Math.PI);
			double v = 0.5 - Math.asin(normal.y()) / Math.PI;
			return Vector.xy(u, v);
		}

		@Override
		public Vec3 n() {
			// This is the geometric normal at the hit point on the sphere.
			return ray().at(t()).sub(c()).div(r).normalized_();
		}

		@Override
		public Material material() {
			return Ball.this.mapMaterial.at(uv());
		}

		@Override
		public Vector uv() {
			Vec3 n = n().normalized_();
			double u = 0.5 + Math.atan2(n.z(), n.x()) / (2 * Math.PI);
			double v = 0.5 - Math.asin(n.y()) / Math.PI;
			return Vector.xy(u, v);
		}
	}
}
