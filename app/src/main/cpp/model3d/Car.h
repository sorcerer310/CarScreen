//
// Created by fengc on 2021/8/16.
//

#ifndef CARSCREEN_CAR_H
#define CARSCREEN_CAR_H

//#include <detail/type_mat.hpp>
//#include <detail/type_mat4x4.hpp>
//#include <shader.h>
//#include <model.h>


class Car {
public:
    Car();
    virtual ~Car();

//    virtual void LoadImage(NativeImage *pImage);

    virtual void Init();
    virtual void Draw(int screenW,int screenH);
    virtual void Destroy();
    virtual void UpdateTransformMatrix(float rotateX,float rotateY,float scaleX,float scaleY);
//    void UpdateMVPMatrix(glm::mat4 &mvpMatrix,int angleX,int angleY,float ratio);

private:
//    glm::mat4 m_MVPMatrix;
//    glm::mat4 m_ModelMatrix;
//    Shader *m_pShader;
//    Model *m_pModel;

    int m_AngleX;
    int m_AngleY;
    float m_ScaleX;
    float m_ScaleY;

};


#endif //CARSCREEN_CAR_H
