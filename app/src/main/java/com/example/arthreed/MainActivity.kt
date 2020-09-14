package com.example.arthreed

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment
    private lateinit var modelUri:Uri
    private var testRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener { view ->
            addObject()
        }

        fragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment;
        modelUri =
            Uri.parse("https://github.com/KhronosGroup/glTF-Sample-Models/blob/master/2.0/AlphaBlendModeTest/glTF/AlphaBlendModeTest.gltf")
        val renderableFuture = ModelRenderable.builder()
            .setSource(
                this, RenderableSource.builder().setSource(
                    this,
                    modelUri,
                    RenderableSource.SourceType.GLTF2
                )
                    .setScale(0.2f) // Scale the original model to 20%.
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId("CesiumMan")
            .build()
        renderableFuture.thenAccept { it -> testRenderable = it }
        renderableFuture.exceptionally { throwable ->
            val toast = Toast.makeText(
                this, "Unable to load renderable ", Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        null}
    }

    private fun addObject() {
        val frame = fragment.arSceneView.arFrame
        val pt = getScreenCenter()
        val hits: List<HitResult>
        if (frame != null && testRenderable != null) {
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane) {
                    val anchor = hit!!.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(fragment.arSceneView.scene)
                    val mNode = TransformableNode(fragment.transformationSystem)
                    mNode.setParent(anchorNode)
                    mNode.renderable = testRenderable
                    mNode.select()
                    break
                } } } }

    private fun getScreenCenter(): android.graphics.Point {
        val vw = findViewById<android.view.View>(android.R.id.content)
        return android.graphics.Point(vw.width / 2, vw.height / 2)
    }
}