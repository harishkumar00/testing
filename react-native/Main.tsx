import * as React from 'react';

import {
  StyleSheet,
  View,
  Text,
  TouchableOpacity,
  Platform,
} from 'react-native';

export default function Main() {
  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={styles.buttonStyles}
        onPress={() => {
          if (Platform.OS === 'ios') {
            console.log('Doorbell: Device is iOS');
          } else {
            console.log('Android');
          }
        }}
      >
        <Text>This is JS Screen</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonStyles: {
    height: 40,
    width: 120,
    backgroundColor: 'blue',
    borderRadius: 10,
    borderWidth: 1,
  },
});
