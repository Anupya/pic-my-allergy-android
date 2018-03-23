package com.example.anupya_pamidimukkala.picmyallergy;

import dagger.Component;

/**
 * Created by Anupya_Pamidimukkala on 3/23/2018.
 */

@Component(modules = AppCopy.class)
interface MyComponent {

    void inject(Upload upload);
}
